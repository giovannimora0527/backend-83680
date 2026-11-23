package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FacturaDetalle;
import com.uniminuto.clinica.entity.FacturaLog;
import com.uniminuto.clinica.entity.FormulaDetalle;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Pago;
import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.GenerarFacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FacturaDetalleRepository;
import com.uniminuto.clinica.repository.FacturaLogRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.FormulaDetalleRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.PagoRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.SesionUsuario;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.service.FacturaService;
import com.uniminuto.clinica.util.PdfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementacion de la facturacion.
 *
 * Reglas: solo una cita atendida genera factura; no se cobra dos veces la misma atencion;
 * los valores quedan copiados en la factura (historico); los medicamentos facturados son
 * solo los efectivamente dispensados; los pagos quedan asociados a la factura; la anulacion
 * no borra nada y deja trazabilidad.
 */
@Service
public class FacturaServiceImpl implements FacturaService {

    /** Metodos de pago permitidos. */
    private static final List<String> METODOS = List.of("EFECTIVO", "TARJETA", "TRANSFERENCIA");

    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private FacturaDetalleRepository detalleRepository;
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private FacturaLogRepository logRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private CitaService citaService;
    @Autowired
    private HistoriaMedicaRepository historiaRepository;
    @Autowired
    private FormulaMedicaRepository formulaRepository;
    @Autowired
    private FormulaDetalleRepository formulaDetalleRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<CitaRs> citasPorFacturar() {
        return citaRepository.findAllByOrderByFechaHoraDesc().stream()
                .filter(c -> "atendida".equals(c.getEstado()))
                .filter(c -> historiaRepository.findByCitaId(c.getId()).stream().anyMatch(h -> h.getTarifa() != null))
                .filter(c -> !facturaRepository.existsByCitaIdAndEstadoNot(c.getId(), Factura.ANULADA))
                .map(citaService::convertirCita).toList();
    }

    @Override
    public FacturaRs previsualizar(Long citaId) {
        Cita cita = this.citaFacturable(citaId);
        Factura factura = this.armarFactura(cita, BigDecimal.ZERO);
        FacturaRs rs = this.convertir(factura, false);
        rs.setLineas(this.calcularLineas(cita).stream().map(this::convertirLinea).toList());
        return rs;
    }

    @Override
    @Transactional
    public FacturaRs generar(GenerarFacturaRq rq) {
        if (rq == null || rq.getCitaId() == null) {
            throw new BadRequestException("Seleccione la cita a facturar");
        }
        Cita cita = this.citaFacturable(rq.getCitaId());
        BigDecimal descuento = rq.getDescuento() == null ? BigDecimal.ZERO : rq.getDescuento().setScale(2, RoundingMode.HALF_UP);
        if (descuento.signum() < 0) {
            throw new BadRequestException("El descuento no puede ser negativo");
        }
        SesionUsuario sesion = UsuarioActual.get();
        if (descuento.signum() > 0 && sesion.getPerfilId() != Perfil.ADMINISTRADOR) {
            throw new BadRequestException("Solo el administrador puede autorizar descuentos", HttpStatus.FORBIDDEN);
        }
        Factura factura = this.armarFactura(cita, descuento);
        if (descuento.compareTo(factura.getSubtotal().add(factura.getIva())) > 0) {
            throw new BadRequestException("El descuento no puede ser mayor al valor de la factura");
        }
        factura.setNumero("TMP-" + System.nanoTime());
        factura.setUsuarioId(sesion.getUsuarioId());
        facturaRepository.save(factura);
        factura.setNumero(String.format("FV-%06d", factura.getId()));
        facturaRepository.save(factura);
        for (FacturaDetalle linea : this.calcularLineas(cita)) {
            linea.setFacturaId(factura.getId());
            detalleRepository.save(linea);
        }
        this.log(factura.getId(), "GENERADA", "Total " + PdfUtil.pesos(factura.getTotal())
                + (descuento.signum() > 0 ? " con descuento de " + PdfUtil.pesos(descuento) : ""));
        return this.consultar(factura.getId());
    }

    @Override
    public List<FacturaRs> listar(Long clienteId, Integer mascotaId, String estado) {
        return facturaRepository.findAllByOrderByFechaDesc().stream()
                .filter(f -> clienteId == null || f.getCliente().getClienteId().equals(clienteId))
                .filter(f -> mascotaId == null || f.getMascota().getMascotaId().equals(mascotaId))
                .filter(f -> estado == null || estado.isBlank() || f.getEstado().equalsIgnoreCase(estado))
                .map(f -> this.convertir(f, false)).toList();
    }

    @Override
    public FacturaRs consultar(Long facturaId) {
        return this.convertir(this.buscar(facturaId), true);
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarPago(PagoRq rq) {
        if (rq == null || rq.getFacturaId() == null) {
            throw new BadRequestException("Seleccione la factura");
        }
        Factura factura = this.buscar(rq.getFacturaId());
        if (Factura.ANULADA.equals(factura.getEstado())) {
            throw new BadRequestException("La factura está anulada; no recibe pagos");
        }
        if (Factura.PAGADA.equals(factura.getEstado())) {
            throw new BadRequestException("La factura ya está pagada");
        }
        BigDecimal saldo = factura.getTotal().subtract(factura.getTotalPagado());
        if (rq.getValor() == null || rq.getValor().signum() <= 0) {
            throw new BadRequestException("El valor del pago debe ser mayor a cero");
        }
        if (rq.getValor().compareTo(saldo) > 0) {
            throw new BadRequestException("El pago no puede ser mayor al saldo pendiente (" + PdfUtil.pesos(saldo) + ")");
        }
        String metodo = rq.getMetodo() == null ? "" : rq.getMetodo().toUpperCase();
        if (!METODOS.contains(metodo)) {
            throw new BadRequestException("El método de pago debe ser EFECTIVO, TARJETA o TRANSFERENCIA");
        }
        if (!"EFECTIVO".equals(metodo) && (rq.getReferencia() == null || rq.getReferencia().isBlank())) {
            throw new BadRequestException("Para pagos con tarjeta o transferencia la referencia es obligatoria");
        }
        Pago pago = new Pago();
        pago.setFacturaId(factura.getId());
        pago.setValor(rq.getValor().setScale(2, RoundingMode.HALF_UP));
        pago.setMetodo(metodo);
        pago.setReferencia(rq.getReferencia());
        pago.setFecha(LocalDateTime.now());
        pago.setUsuarioId(UsuarioActual.get().getUsuarioId());
        pagoRepository.save(pago);

        factura.setTotalPagado(factura.getTotalPagado().add(pago.getValor()));
        factura.setEstado(factura.getTotalPagado().compareTo(factura.getTotal()) >= 0 ? Factura.PAGADA : Factura.PAGO_PARCIAL);
        facturaRepository.save(factura);
        this.log(factura.getId(), "PAGO", metodo + " por " + PdfUtil.pesos(pago.getValor()) + ". Estado: " + factura.getEstado());
        return this.exito(Factura.PAGADA.equals(factura.getEstado()) ? "Pago registrado. La factura quedó pagada."
                : "Pago parcial registrado. Saldo: " + PdfUtil.pesos(factura.getTotal().subtract(factura.getTotalPagado())));
    }

    @Override
    @Transactional
    public MiRespuestaRS anular(Long facturaId, String motivo) {
        SesionUsuario sesion = UsuarioActual.get();
        if (sesion.getPerfilId() != Perfil.ADMINISTRADOR) {
            throw new BadRequestException("Solo el administrador puede anular facturas", HttpStatus.FORBIDDEN);
        }
        if (motivo == null || motivo.trim().length() < 10) {
            throw new BadRequestException("Escriba el motivo de la anulación (mínimo 10 caracteres)");
        }
        Factura factura = this.buscar(facturaId);
        if (Factura.ANULADA.equals(factura.getEstado())) {
            throw new BadRequestException("La factura ya estaba anulada");
        }
        factura.setEstado(Factura.ANULADA);
        factura.setMotivoAnulacion(motivo.trim());
        factura.setFechaAnulacion(LocalDateTime.now());
        factura.setUsuarioAnulacionId(sesion.getUsuarioId());
        facturaRepository.save(factura);
        this.log(factura.getId(), "ANULADA", motivo.trim());
        return this.exito("Factura anulada. Los registros históricos se conservan.");
    }

    @Override
    public byte[] facturaPdf(Long facturaId) {
        FacturaRs f = this.consultar(facturaId);
        DateTimeFormatter fh = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<List<String>> filas = f.getLineas().stream().map(l -> List.of(l.getTipo(), l.getDescripcion(),
                String.valueOf(l.getCantidad()), PdfUtil.pesos(l.getPrecioUnitario()), PdfUtil.pesos(l.getSubtotal()),
                l.getIvaPorcentaje().stripTrailingZeros().toPlainString() + "%", PdfUtil.pesos(l.getIvaValor()),
                PdfUtil.pesos(l.getTotal()))).toList();
        PdfUtil.Documento doc = new PdfUtil.Documento("Factura de venta " + f.getNumero() + "  ·  Estado: " + f.getEstado())
                .seccion("Propietario y paciente", List.of(
                        new String[]{"Propietario", f.getClienteNombre()},
                        new String[]{"Documento", f.getClienteDocumento()},
                        new String[]{"Teléfono", f.getClienteTelefono() == null ? "-" : f.getClienteTelefono()},
                        new String[]{"Dirección", f.getClienteDireccion() == null ? "-" : f.getClienteDireccion()},
                        new String[]{"Mascota", f.getMascotaNombre() + (f.getMascotaEspecie() == null ? "" : " (" + f.getMascotaEspecie() + ")")},
                        new String[]{"Fecha de la consulta", f.getFechaCita().format(fh)}))
                .seccion("Atención", List.of(
                        new String[]{"Médico veterinario", f.getMedicoNombre()},
                        new String[]{"Especialidad", f.getEspecialidad() == null ? "-" : f.getEspecialidad()},
                        new String[]{"Fecha de la factura", f.getFecha().format(fh)},
                        new String[]{"Generada por", f.getUsuario()}))
                .tabla("Detalle", List.of("Tipo", "Descripción", "Cant.", "Valor unit.", "Subtotal", "IVA %", "IVA", "Total"), filas)
                .totales(List.of(new String[]{"Subtotal", PdfUtil.pesos(f.getSubtotal())},
                        new String[]{"IVA", PdfUtil.pesos(f.getIva())},
                        new String[]{"Descuento", "- " + PdfUtil.pesos(f.getDescuento())},
                        new String[]{"TOTAL A PAGAR", PdfUtil.pesos(f.getTotal())},
                        new String[]{"Pagado", PdfUtil.pesos(f.getTotalPagado())},
                        new String[]{"Saldo", PdfUtil.pesos(f.getSaldo())}));
        if (!f.getPagos().isEmpty()) {
            doc.tabla("Pagos registrados", List.of("Fecha", "Método", "Referencia", "Valor"), f.getPagos().stream()
                    .map(p -> List.of(p.getFecha().format(fh), p.getMetodo(), p.getReferencia() == null ? "" : p.getReferencia(),
                            PdfUtil.pesos(p.getValor()))).toList());
        }
        if (Factura.ANULADA.equals(f.getEstado())) {
            doc.texto("FACTURA ANULADA", "Anulada el " + f.getFechaAnulacion().format(fh) + ". Motivo: " + f.getMotivoAnulacion());
        }
        return doc.generar();
    }

    // ------------------------------------------------------------------ calculos

    /**
     * Valida que la cita se pueda facturar.
     */
    private Cita citaFacturable(Long citaId) {
        Cita cita = citaService.buscarCita(citaId);
        if (!"atendida".equals(cita.getEstado())) {
            throw new BadRequestException("Solo se puede facturar una cita atendida");
        }
        if (facturaRepository.existsByCitaIdAndEstadoNot(cita.getId(), Factura.ANULADA)) {
            throw new BadRequestException("Esta atención ya fue facturada (no se cobra dos veces el mismo servicio)");
        }
        return cita;
    }

    /**
     * Arma la cabecera de la factura con los totales calculados.
     */
    private Factura armarFactura(Cita cita, BigDecimal descuento) {
        List<FacturaDetalle> lineas = this.calcularLineas(cita);
        BigDecimal subtotal = lineas.stream().map(FacturaDetalle::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal iva = lineas.stream().map(FacturaDetalle::getIvaValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        Factura factura = new Factura();
        factura.setCita(cita);
        factura.setCliente(cita.getCliente());
        factura.setMascota(cita.getMascota());
        factura.setMedico(cita.getMedico());
        factura.setEspecialidad(cita.getMedico().getEspecializacion() == null ? null
                : cita.getMedico().getEspecializacion().getNombre());
        factura.setFecha(LocalDateTime.now());
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setDescuento(descuento);
        factura.setTotal(subtotal.add(iva).subtract(descuento).max(BigDecimal.ZERO));
        factura.setTotalPagado(BigDecimal.ZERO);
        factura.setEstado(Factura.PENDIENTE);
        return factura;
    }

    /**
     * Lineas: el servicio prestado (tarifa de la historia) y los medicamentos
     * efectivamente dispensados de la formula de la cita.
     */
    private List<FacturaDetalle> calcularLineas(Cita cita) {
        List<FacturaDetalle> lineas = new ArrayList<>();
        HistoriaMedica historia = historiaRepository.findByCitaId(cita.getId()).stream().findFirst()
                .orElseThrow(() -> new BadRequestException("La cita no tiene atención registrada"));
        Tarifa tarifa = historia.getTarifa();
        if (tarifa == null) {
            throw new BadRequestException("La atención no tiene un servicio asociado para cobrar");
        }
        lineas.add(this.linea(FacturaDetalle.SERVICIO, tarifa.getId(), tarifa.getNombre(), 1, tarifa.getValor(), tarifa.getIvaPorcentaje()));
        for (FormulaMedica formula : formulaRepository.findAll().stream()
                // solo la formula generada en la atencion (las del modelo anterior no tienen historia)
                .filter(f -> f.getCita().getId().equals(cita.getId()) && f.getHistoriaId() != null).toList()) {
            for (FormulaDetalle d : formulaDetalleRepository.findByFormulaIdOrderByIdAsc(formula.getId())) {
                if (d.getCantidadEntregada() > 0) {
                    Medicamento m = d.getMedicamento();
                    lineas.add(this.linea(FacturaDetalle.MEDICAMENTO, m.getId(), m.getNombre() + " (" + m.getCodigo() + ")",
                            d.getCantidadEntregada(), m.getPrecioVenta(), m.getIvaPorcentaje()));
                }
            }
        }
        return lineas;
    }

    /**
     * Calcula una linea: subtotal, IVA y total.
     */
    private FacturaDetalle linea(String tipo, Long referencia, String descripcion, int cantidad, BigDecimal precio,
                                 BigDecimal ivaPorcentaje) {
        BigDecimal iva = ivaPorcentaje == null ? BigDecimal.ZERO : ivaPorcentaje;
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ivaValor = subtotal.multiply(iva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        FacturaDetalle d = new FacturaDetalle();
        d.setTipo(tipo);
        d.setReferenciaId(referencia);
        d.setDescripcion(descripcion);
        d.setCantidad(cantidad);
        d.setPrecioUnitario(precio);
        d.setIvaPorcentaje(iva);
        d.setSubtotal(subtotal);
        d.setIvaValor(ivaValor);
        d.setTotal(subtotal.add(ivaValor));
        return d;
    }

    // ------------------------------------------------------------------ conversion

    private FacturaRs convertir(Factura f, boolean completa) {
        Map<Long, String> usuarios = usuarioRepository.findAll().stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getUsername));
        FacturaRs rs = new FacturaRs();
        rs.setId(f.getId());
        rs.setNumero(f.getNumero());
        rs.setCitaId(f.getCita().getId());
        rs.setFechaCita(f.getCita().getFechaHora());
        rs.setClienteId(f.getCliente().getClienteId());
        rs.setClienteNombre(f.getCliente().getNombres() + " " + f.getCliente().getApellidos());
        rs.setClienteDocumento(f.getCliente().getTipoDocumento() + " " + f.getCliente().getNumeroDocumento());
        rs.setClienteTelefono(f.getCliente().getTelefono());
        rs.setClienteDireccion(f.getCliente().getDireccion());
        rs.setMascotaId(f.getMascota().getMascotaId());
        rs.setMascotaNombre(f.getMascota().getNombreMascota());
        if (f.getMascota().getRaza() != null) {
            rs.setMascotaEspecie(f.getMascota().getRaza().getEspecie());
        }
        rs.setMedicoId(f.getMedico().getId());
        rs.setMedicoNombre(f.getMedico().getNombres() + " " + f.getMedico().getApellidos());
        rs.setEspecialidad(f.getEspecialidad());
        rs.setFecha(f.getFecha());
        rs.setSubtotal(f.getSubtotal());
        rs.setIva(f.getIva());
        rs.setDescuento(f.getDescuento());
        rs.setTotal(f.getTotal());
        rs.setTotalPagado(f.getTotalPagado());
        rs.setSaldo(Factura.ANULADA.equals(f.getEstado()) ? BigDecimal.ZERO : f.getTotal().subtract(f.getTotalPagado()));
        rs.setEstado(f.getEstado());
        rs.setUsuario(usuarios.get(f.getUsuarioId()));
        rs.setMotivoAnulacion(f.getMotivoAnulacion());
        rs.setFechaAnulacion(f.getFechaAnulacion());
        if (completa && f.getId() != null) {
            rs.setLineas(facturaRepository.findDetalles(f.getId()).stream().map(this::convertirLinea).toList());
            rs.setPagos(facturaRepository.findPagos(f.getId()).stream().map(p -> {
                FacturaRs.PagoLinea pl = new FacturaRs.PagoLinea();
                pl.setId(p.getId());
                pl.setValor(p.getValor());
                pl.setMetodo(p.getMetodo());
                pl.setReferencia(p.getReferencia());
                pl.setFecha(p.getFecha());
                pl.setUsuario(usuarios.get(p.getUsuarioId()));
                return pl;
            }).toList());
            rs.setLog(facturaRepository.findLog(f.getId()).stream().map(l -> {
                FacturaRs.LogLinea ll = new FacturaRs.LogLinea();
                ll.setAccion(l.getAccion());
                ll.setDetalle(l.getDetalle());
                ll.setUsuario(usuarios.get(l.getUsuarioId()));
                ll.setFecha(l.getFecha());
                return ll;
            }).toList());
        }
        return rs;
    }

    private FacturaRs.Linea convertirLinea(FacturaDetalle d) {
        FacturaRs.Linea l = new FacturaRs.Linea();
        l.setTipo(d.getTipo());
        l.setDescripcion(d.getDescripcion());
        l.setCantidad(d.getCantidad());
        l.setPrecioUnitario(d.getPrecioUnitario());
        l.setIvaPorcentaje(d.getIvaPorcentaje());
        l.setSubtotal(d.getSubtotal());
        l.setIvaValor(d.getIvaValor());
        l.setTotal(d.getTotal());
        return l;
    }

    private Factura buscar(Long facturaId) {
        if (facturaId == null) {
            throw new BadRequestException("El id de la factura es obligatorio");
        }
        return facturaRepository.findById(facturaId)
                .orElseThrow(() -> new BadRequestException("Factura no encontrada", HttpStatus.NOT_FOUND));
    }

    private void log(Long facturaId, String accion, String detalle) {
        FacturaLog log = new FacturaLog();
        log.setFacturaId(facturaId);
        log.setAccion(accion);
        log.setDetalle(detalle);
        log.setUsuarioId(UsuarioActual.get().getUsuarioId());
        log.setFecha(LocalDateTime.now());
        logRepository.save(log);
    }

    private MiRespuestaRS exito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
