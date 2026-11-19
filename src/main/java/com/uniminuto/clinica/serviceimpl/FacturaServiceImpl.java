package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.DetalleFactura;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.PagoFactura;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.DetalleServicioRq;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoFacturaRq;
import com.uniminuto.clinica.models.PreliquidacionFacturaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.service.FacturaService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
/** Implementa las reglas de negocio de facturación, precios históricos, impuestos, abonos y anulación. */
public class FacturaServiceImpl implements FacturaService {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal IVA_POR_DEFECTO = new BigDecimal("19.00");
    private static final BigDecimal TARIFA_CONSULTA_GENERAL = new BigDecimal("60000.00");
    private static final BigDecimal TARIFA_CONSULTA_ESPECIALIZADA = new BigDecimal("85000.00");

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    public FacturaServiceImpl() {
    }

    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              CitaRepository citaRepository,
                              FormulaMedicaRepository formulaMedicaRepository,
                              ClienteRepository clienteRepository,
                              MascotaRepository mascotaRepository) {
        this.facturaRepository = facturaRepository;
        this.citaRepository = citaRepository;
        this.formulaMedicaRepository = formulaMedicaRepository;
        this.clienteRepository = clienteRepository;
        this.mascotaRepository = mascotaRepository;
    }

    @Override
    public List<Factura> listarFacturas(Long clienteId, Integer mascotaId, String estado,
                                        LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        List<Factura> facturas;
        if (fechaInicial != null || fechaFinal != null) {
            if (fechaInicial == null || fechaFinal == null || fechaInicial.isAfter(fechaFinal)) {
                throw new BadRequestException("El rango de fechas de facturación no es válido");
            }
            facturas = facturaRepository.findByFechaGeneracionBetweenOrderByFechaGeneracionDesc(fechaInicial, fechaFinal);
        } else if (mascotaId != null) {
            facturas = listarHistorialPorMascota(mascotaId);
        } else if (clienteId != null) {
            facturas = listarHistorialPorCliente(clienteId);
        } else {
            facturas = facturaRepository.findAllByOrderByFechaGeneracionDesc();
        }

        if (clienteId != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getCliente() != null && clienteId.equals(f.getCliente().getClienteId()))
                    .collect(Collectors.toList());
        }
        if (mascotaId != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getMascota() != null && mascotaId.equals(f.getMascota().getMascotaId()))
                    .collect(Collectors.toList());
        }
        if (estado != null && !estado.isBlank()) {
            String estadoFiltro = estado.trim().toUpperCase();
            facturas = facturas.stream()
                    .filter(f -> estadoFiltro.equalsIgnoreCase(f.getEstado()))
                    .collect(Collectors.toList());
        }
        return facturas;
    }

    @Override
    public Factura consultarFactura(Long facturaId) {
        if (facturaId == null) {
            throw new BadRequestException("El ID de la factura es obligatorio");
        }
        return buscarFactura(facturaId);
    }

    @Override
    public List<Factura> listarHistorialPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new BadRequestException("El ID del cliente es obligatorio");
        }
        if (!clienteRepository.existsById(clienteId)) {
            throw new NotFoundException("Cliente no encontrado");
        }
        return facturaRepository.findByCliente_ClienteIdOrderByFechaGeneracionDesc(clienteId);
    }

    @Override
    public List<Factura> listarHistorialPorMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new BadRequestException("El ID de la mascota es obligatorio");
        }
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("Mascota no encontrada");
        }
        return facturaRepository.findByMascota_MascotaIdOrderByFechaGeneracionDesc(mascotaId);
    }

    @Override
    public PreliquidacionFacturaRS preliquidarCita(Long citaId) {
        Cita cita = validarCitaParaFacturar(citaId);

        PreliquidacionFacturaRS pre = new PreliquidacionFacturaRS();
        pre.setCita(cita);
        pre.setCliente(cita.getCliente());
        pre.setMascota(cita.getMascota());
        pre.setMedico(cita.getMedico());
        pre.setPorcentajeImpuestoSugerido(IVA_POR_DEFECTO);

        // 1. Servicio médico principal sugerido según especialidad del veterinario
        DetalleFactura servicioPrincipal = construirServicioConsultaPorDefecto(cita);
        pre.getServiciosSugeridos().add(servicioPrincipal);
        pre.setSubtotalServicios(servicioPrincipal.getSubtotalItem());

        // 2. Medicamentos efectivamente dispensados en las fórmulas activas de la cita
        List<FormulaMedica> formulasCita = formulaMedicaRepository.findByCita_IdOrderByFechaCreacionRegistroDesc(cita.getId());
        BigDecimal sumaMeds = BigDecimal.ZERO;

        for (FormulaMedica formula : formulasCita) {
            if (!Boolean.TRUE.equals(formula.getActivo())) {
                continue;
            }
            int entregada = formula.getCantidadEntregada() != null ? formula.getCantidadEntregada() : 0;
            int solicitada = formula.getCantidadSolicitada() != null ? formula.getCantidadSolicitada() : 0;

            if (entregada > 0) {
                DetalleFactura detMed = construirDetalleMedicamentoDispensado(formula);
                pre.getMedicamentosDispensados().add(detMed);
                sumaMeds = sumaMeds.add(detMed.getSubtotalItem());
            }
            if (entregada < solicitada) {
                pre.getFormulasPendientesDispensar().add(formula);
            }
        }
        pre.setSubtotalMedicamentos(sumaMeds.setScale(2, RoundingMode.HALF_UP));
        return pre;
    }

    @Override
    @Transactional
    public MiRespuestaRS crearFactura(FacturaRq facturaRq) {
        if (facturaRq == null || facturaRq.getCitaId() == null) {
            throw new BadRequestException("Debe seleccionar una cita atendida para generar la factura");
        }

        // Regla 3.3: Solo una cita atendida puede generar factura y no se cobra dos veces la misma atención
        Cita cita = validarCitaParaFacturar(facturaRq.getCitaId());

        Factura factura = new Factura();
        factura.setCita(cita);
        factura.setCliente(cita.getCliente());
        factura.setMascota(cita.getMascota());
        factura.setMedico(cita.getMedico());
        factura.setFechaGeneracion(LocalDateTime.now());
        factura.setNumeroFactura(generarNumeroFactura(cita.getId()));
        factura.setObservaciones(facturaRq.getObservaciones() != null ? facturaRq.getObservaciones().trim() : null);

        List<DetalleFactura> detalles = new ArrayList<>();

        // 1. Procesar y validar servicios (evitando cobrar dos veces el mismo servicio)
        BigDecimal subtotalServicios = BigDecimal.ZERO;
        List<DetalleServicioRq> serviciosRq = facturaRq.getServicios();
        if (serviciosRq == null || serviciosRq.isEmpty()) {
            DetalleFactura servicioDefecto = construirServicioConsultaPorDefecto(cita);
            servicioDefecto.setFactura(factura);
            detalles.add(servicioDefecto);
            subtotalServicios = subtotalServicios.add(servicioDefecto.getSubtotalItem());
        } else {
            Set<String> codigosServicioUsados = new HashSet<>();
            Set<String> conceptosServicioUsados = new HashSet<>();

            for (DetalleServicioRq srv : serviciosRq) {
                if (srv == null || srv.getConcepto() == null || srv.getConcepto().isBlank()) {
                    throw new BadRequestException("El concepto de cada servicio es obligatorio");
                }
                if (srv.getPrecioUnitario() == null || srv.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BadRequestException("El precio unitario del servicio '" + srv.getConcepto() + "' debe ser mayor que cero");
                }
                int cantidadSrv = (srv.getCantidad() == null || srv.getCantidad() <= 0) ? 1 : srv.getCantidad();
                if (cantidadSrv > 1) {
                    throw new BadRequestException("No se puede cobrar dos veces el mismo servicio en la misma atención (" + srv.getConcepto() + ")");
                }

                String codigoNorm = (srv.getCodigoServicio() != null && !srv.getCodigoServicio().isBlank())
                        ? srv.getCodigoServicio().trim().toUpperCase(Locale.ROOT)
                        : srv.getConcepto().trim().toUpperCase(Locale.ROOT);
                String conceptoNorm = srv.getConcepto().trim().toUpperCase(Locale.ROOT);

                if (!codigosServicioUsados.add(codigoNorm) || !conceptosServicioUsados.add(conceptoNorm)) {
                    throw new ConflictException("No se puede cobrar dos veces el mismo servicio: " + srv.getConcepto());
                }

                BigDecimal precioHist = srv.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP);
                BigDecimal descItem = srv.getDescuentoItem() != null && srv.getDescuentoItem().compareTo(BigDecimal.ZERO) > 0
                        ? srv.getDescuentoItem().setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                BigDecimal brutoItem = precioHist.multiply(BigDecimal.valueOf(cantidadSrv)).setScale(2, RoundingMode.HALF_UP);
                if (descItem.compareTo(brutoItem) > 0) {
                    throw new BadRequestException("El descuento del servicio no puede superar su valor");
                }
                BigDecimal subItem = brutoItem.subtract(descItem).setScale(2, RoundingMode.HALF_UP);

                DetalleFactura detSrv = new DetalleFactura();
                detSrv.setFactura(factura);
                detSrv.setTipoItem(Constantes.ITEM_SERVICIO);
                detSrv.setCodigoServicio(codigoNorm);
                detSrv.setConcepto(srv.getConcepto().trim());
                detSrv.setCantidad(cantidadSrv);
                detSrv.setPrecioUnitarioHistorico(precioHist);
                detSrv.setDescuentoItem(descItem);
                detSrv.setSubtotalItem(subItem);
                detalles.add(detSrv);

                subtotalServicios = subtotalServicios.add(subItem);
            }
        }

        // 2. Regla 3.3: Facturar SOLO medicamentos efectivamente dispensados y conservar sus precios históricos
        BigDecimal subtotalMedicamentos = BigDecimal.ZERO;
        List<FormulaMedica> formulasCita = formulaMedicaRepository.findByCita_IdOrderByFechaCreacionRegistroDesc(cita.getId());
        if (formulasCita != null) {
            for (FormulaMedica formula : formulasCita) {
                if (!Boolean.TRUE.equals(formula.getActivo())) {
                    continue;
                }
                int cantidadDispensada = formula.getCantidadEntregada() != null ? formula.getCantidadEntregada() : 0;
                // Solo se incluyen medicamentos efectivamente dispensados (cantidadEntregada > 0)
                if (cantidadDispensada > 0) {
                    DetalleFactura detMed = construirDetalleMedicamentoDispensado(formula);
                    detMed.setFactura(factura);
                    detalles.add(detMed);
                    subtotalMedicamentos = subtotalMedicamentos.add(detMed.getSubtotalItem());
                }
            }
        }

        subtotalServicios = subtotalServicios.setScale(2, RoundingMode.HALF_UP);
        subtotalMedicamentos = subtotalMedicamentos.setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = subtotalServicios.add(subtotalMedicamentos).setScale(2, RoundingMode.HALF_UP);

        // 3. Cálculo de descuento general
        BigDecimal porcentajeDescuento = BigDecimal.ZERO;
        BigDecimal valorDescuento = BigDecimal.ZERO;

        if (facturaRq.getPorcentajeDescuento() != null && facturaRq.getPorcentajeDescuento().compareTo(BigDecimal.ZERO) > 0) {
            if (facturaRq.getPorcentajeDescuento().compareTo(CIEN) > 0) {
                throw new BadRequestException("El porcentaje de descuento no puede superar el 100%");
            }
            porcentajeDescuento = facturaRq.getPorcentajeDescuento().setScale(2, RoundingMode.HALF_UP);
            valorDescuento = subtotal.multiply(porcentajeDescuento).divide(CIEN, 2, RoundingMode.HALF_UP);
        } else if (facturaRq.getValorDescuento() != null && facturaRq.getValorDescuento().compareTo(BigDecimal.ZERO) > 0) {
            valorDescuento = facturaRq.getValorDescuento().setScale(2, RoundingMode.HALF_UP);
            if (valorDescuento.compareTo(subtotal) > 0) {
                throw new BadRequestException("El valor del descuento no puede ser mayor al subtotal de la factura");
            }
            if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
                porcentajeDescuento = valorDescuento.multiply(CIEN).divide(subtotal, 2, RoundingMode.HALF_UP);
            }
        }

        // 4. Cálculo de impuestos según configuración aplicada
        BigDecimal porcentajeImpuesto = facturaRq.getPorcentajeImpuesto() != null
                ? facturaRq.getPorcentajeImpuesto().setScale(2, RoundingMode.HALF_UP)
                : IVA_POR_DEFECTO;
        if (porcentajeImpuesto.compareTo(BigDecimal.ZERO) < 0 || porcentajeImpuesto.compareTo(CIEN) > 0) {
            throw new BadRequestException("El porcentaje de impuesto debe estar entre 0% y 100%");
        }

        BigDecimal baseGravable = subtotal.subtract(valorDescuento).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorImpuesto = baseGravable.multiply(porcentajeImpuesto).divide(CIEN, 2, RoundingMode.HALF_UP);
        BigDecimal total = baseGravable.add(valorImpuesto).setScale(2, RoundingMode.HALF_UP);

        factura.setSubtotalServicios(subtotalServicios);
        factura.setSubtotalMedicamentos(subtotalMedicamentos);
        factura.setSubtotal(subtotal);
        factura.setPorcentajeDescuento(porcentajeDescuento);
        factura.setValorDescuento(valorDescuento);
        factura.setPorcentajeImpuesto(porcentajeImpuesto);
        factura.setValorImpuesto(valorImpuesto);
        factura.setTotal(total);
        factura.setTotalPagado(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        factura.setSaldoPendiente(total);
        factura.setEstado(Constantes.FACTURA_PENDIENTE);
        factura.setDetalles(detalles);

        String usuario = (facturaRq.getUsuarioRegistro() != null && !facturaRq.getUsuarioRegistro().isBlank())
                ? facturaRq.getUsuarioRegistro().trim()
                : "FACTURACION";
        factura.setHistorialOperaciones(construirLineaTrazabilidad(null, usuario,
                "Factura " + factura.getNumeroFactura() + " generada sobre Cita #" + cita.getId()
                        + " | Subtotal: $" + subtotal + " | Descuento: $" + valorDescuento
                        + " | Impuesto (" + porcentajeImpuesto + "%): $" + valorImpuesto + " | Total: $" + total));

        // 5. Pago o abono inicial opcional al momento de emitir la factura
        if (facturaRq.getMontoPagoInicial() != null && facturaRq.getMontoPagoInicial().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal montoInicial = facturaRq.getMontoPagoInicial().setScale(2, RoundingMode.HALF_UP);
            if (montoInicial.compareTo(total) > 0) {
                throw new BadRequestException("El monto del pago inicial ($" + montoInicial
                        + ") no puede superar el total de la factura ($" + total + ")");
            }
            String metodo = normalizarMetodoPago(facturaRq.getMetodoPagoInicial());

            PagoFactura pagoInicial = new PagoFactura();
            pagoInicial.setFactura(factura);
            pagoInicial.setFechaPago(LocalDateTime.now());
            pagoInicial.setMonto(montoInicial);
            pagoInicial.setMetodoPago(metodo);
            pagoInicial.setReferenciaPago(limpiarTexto(facturaRq.getReferenciaPagoInicial()));
            pagoInicial.setUsuarioRegistro(usuario);
            pagoInicial.setObservacion("Pago registrado al generar la factura");
            factura.getPagos().add(pagoInicial);

            factura.setTotalPagado(montoInicial);
            BigDecimal nuevoSaldo = total.subtract(montoInicial).setScale(2, RoundingMode.HALF_UP);
            factura.setSaldoPendiente(nuevoSaldo);
            factura.setEstado(nuevoSaldo.compareTo(BigDecimal.ZERO) == 0
                    ? Constantes.FACTURA_PAGADA
                    : Constantes.FACTURA_PARCIAL);

            factura.setHistorialOperaciones(construirLineaTrazabilidad(factura.getHistorialOperaciones(), usuario,
                    "Pago inicial registrado por $" + montoInicial + " (" + metodo + ") | Estado: " + factura.getEstado()));
        }

        facturaRepository.save(factura);
        return respuesta(HttpStatus.CREATED.value(),
                "Factura " + factura.getNumeroFactura() + " generada correctamente en estado " + factura.getEstado());
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarPago(Long facturaId, PagoFacturaRq pagoRq) {
        if (facturaId == null) {
            throw new BadRequestException("El identificador de la factura es obligatorio");
        }
        if (pagoRq == null || pagoRq.getMonto() == null || pagoRq.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto del pago o abono debe ser mayor que cero");
        }

        Factura factura = buscarFactura(facturaId);
        if (Constantes.FACTURA_ANULADA.equalsIgnoreCase(factura.getEstado())) {
            throw new ConflictException("No se pueden registrar pagos sobre una factura anulada");
        }
        if (Constantes.FACTURA_PAGADA.equalsIgnoreCase(factura.getEstado())
                || factura.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ConflictException("La factura ya se encuentra pagada en su totalidad");
        }

        BigDecimal monto = pagoRq.getMonto().setScale(2, RoundingMode.HALF_UP);
        if (monto.compareTo(factura.getSaldoPendiente()) > 0) {
            throw new BadRequestException("El monto a abonar ($" + monto
                    + ") no puede superar el saldo pendiente de la factura ($" + factura.getSaldoPendiente() + ")");
        }

        String metodo = normalizarMetodoPago(pagoRq.getMetodoPago());
        String usuario = (pagoRq.getUsuarioRegistro() != null && !pagoRq.getUsuarioRegistro().isBlank())
                ? pagoRq.getUsuarioRegistro().trim()
                : "CAJA / FACTURACION";

        PagoFactura pago = new PagoFactura();
        pago.setFactura(factura);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMonto(monto);
        pago.setMetodoPago(metodo);
        pago.setReferenciaPago(limpiarTexto(pagoRq.getReferenciaPago()));
        pago.setUsuarioRegistro(usuario);
        pago.setObservacion(limpiarTexto(pagoRq.getObservacion()));
        factura.getPagos().add(pago);

        BigDecimal nuevoPagado = factura.getTotalPagado().add(monto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nuevoSaldo = factura.getTotal().subtract(nuevoPagado).setScale(2, RoundingMode.HALF_UP);
        String estadoAnterior = factura.getEstado();
        String nuevoEstado = nuevoSaldo.compareTo(BigDecimal.ZERO) == 0
                ? Constantes.FACTURA_PAGADA
                : Constantes.FACTURA_PARCIAL;

        factura.setTotalPagado(nuevoPagado);
        factura.setSaldoPendiente(nuevoSaldo);
        factura.setEstado(nuevoEstado);
        factura.setHistorialOperaciones(construirLineaTrazabilidad(factura.getHistorialOperaciones(), usuario,
                "Abono/Pago registrado por $" + monto + " (" + metodo + ") | Saldo restante: $" + nuevoSaldo
                        + " | Estado: " + estadoAnterior + " -> " + nuevoEstado));

        facturaRepository.save(factura);
        return respuesta(HttpStatus.OK.value(),
                "Pago registrado correctamente. Estado de la factura: " + nuevoEstado + " (Saldo: $" + nuevoSaldo + ")");
    }

    @Override
    @Transactional
    public MiRespuestaRS anularFactura(Long facturaId, String motivo, String usuario) {
        if (facturaId == null) {
            throw new BadRequestException("El identificador de la factura es obligatorio");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new BadRequestException("Debe indicar el motivo de anulación de la factura");
        }

        Factura factura = buscarFactura(facturaId);
        if (Constantes.FACTURA_ANULADA.equalsIgnoreCase(factura.getEstado())) {
            throw new ConflictException("La factura ya se encuentra anulada");
        }

        String estadoPrevio = factura.getEstado();
        String autor = (usuario != null && !usuario.isBlank()) ? usuario.trim() : "ADMINISTRACION";

        // Regla 3.3: Controlar anulación de factura sin eliminar el historial ni los detalles
        factura.setEstado(Constantes.FACTURA_ANULADA);
        factura.setMotivoAnulacion(motivo.trim());
        factura.setFechaAnulacion(LocalDateTime.now());
        factura.setSaldoPendiente(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        factura.setHistorialOperaciones(construirLineaTrazabilidad(factura.getHistorialOperaciones(), autor,
                "Factura ANULADA (estado anterior: " + estadoPrevio + ") | Motivo: " + motivo.trim()));

        facturaRepository.save(factura);
        return respuesta(HttpStatus.OK.value(),
                "Factura " + factura.getNumeroFactura() + " anulada correctamente conservando su historial");
    }

    private Cita validarCitaParaFacturar(Long citaId) {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita es obligatorio");
        }
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));

        // Regla: Solo una cita atendida podrá generar factura
        if (!Constantes.CITA_ATENDIDA.equalsIgnoreCase(cita.getEstado())
                && !Constantes.CITA_FINALIZADA.equalsIgnoreCase(cita.getEstado())) {
            throw new BadRequestException("Solo se puede generar factura para una cita que ya fue ATENDIDA (estado actual: "
                    + cita.getEstado() + ")");
        }

        // Regla: No cobrar dos veces el mismo servicio / atención
        if (facturaRepository.existsByCita_IdAndEstadoNot(cita.getId(), Constantes.FACTURA_ANULADA)) {
            throw new ConflictException("La cita #" + cita.getId()
                    + " ya tiene una factura activa emitida; no se puede cobrar dos veces el mismo servicio");
        }
        return cita;
    }

    private DetalleFactura construirServicioConsultaPorDefecto(Cita cita) {
        Medico medico = cita.getMedico();
        String espNombre = (medico != null && medico.getEspecializacion() != null
                && medico.getEspecializacion().getNombre() != null)
                ? medico.getEspecializacion().getNombre().trim()
                : "General";
        boolean esEspecializada = !espNombre.equalsIgnoreCase("General")
                && !espNombre.equalsIgnoreCase("Medicina General");
        BigDecimal tarifa = esEspecializada ? TARIFA_CONSULTA_ESPECIALIZADA : TARIFA_CONSULTA_GENERAL;

        DetalleFactura det = new DetalleFactura();
        det.setTipoItem(Constantes.ITEM_SERVICIO);
        det.setCodigoServicio("CONSULTA_CITA_" + cita.getId());
        det.setConcepto("Consulta Médica Veterinaria (" + espNombre + ") - Cita #" + cita.getId());
        det.setCantidad(1);
        det.setPrecioUnitarioHistorico(tarifa);
        det.setDescuentoItem(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        det.setSubtotalItem(tarifa);
        return det;
    }

    private DetalleFactura construirDetalleMedicamentoDispensado(FormulaMedica formula) {
        Medicamento med = formula.getMedicamento();
        int cantidadDispensada = formula.getCantidadEntregada() != null ? formula.getCantidadEntregada() : 0;
        BigDecimal precioHistorico = (med != null && med.getPrecioVenta() != null)
                ? med.getPrecioVenta().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = precioHistorico.multiply(BigDecimal.valueOf(cantidadDispensada))
                .setScale(2, RoundingMode.HALF_UP);

        DetalleFactura det = new DetalleFactura();
        det.setTipoItem(Constantes.ITEM_MEDICAMENTO);
        det.setFormulaMedicaId(formula.getId());
        det.setMedicamentoId(med != null ? med.getId() : null);
        det.setConcepto("Medicamento dispensado: " + (med != null ? med.getNombre() : "Medicamento")
                + (med != null && med.getPresentacion() != null ? " (" + med.getPresentacion() + ")" : "")
                + " - Fórmula #" + formula.getId());
        det.setCantidad(cantidadDispensada);
        det.setPrecioUnitarioHistorico(precioHistorico);
        det.setDescuentoItem(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        det.setSubtotalItem(subtotal);
        return det;
    }

    private Factura buscarFactura(Long facturaId) {
        return facturaRepository.findById(facturaId)
                .orElseThrow(() -> new NotFoundException("Factura no encontrada"));
    }

    private String normalizarMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            return "EFECTIVO";
        }
        String limpio = metodoPago.trim().toUpperCase(Locale.ROOT);
        if (!Constantes.METODOS_PAGO.contains(limpio)) {
            throw new BadRequestException("El método de pago no es válido. Valores permitidos: " + Constantes.METODOS_PAGO);
        }
        return limpio;
    }

    private String generarNumeroFactura(Long citaId) {
        long consecutivo = facturaRepository.count() + 1;
        return String.format("FAC-%d-C%d-%04d", LocalDateTime.now().getYear(), citaId, consecutivo);
    }

    private String construirLineaTrazabilidad(String historialActual, String usuario, String detalle) {
        String autor = (usuario != null && !usuario.isBlank()) ? " (" + usuario.trim() + ")" : "";
        String nuevaLinea = "[" + LocalDateTime.now().format(FORMATO_FECHA_HORA) + "]" + autor + " " + detalle;
        if (historialActual == null || historialActual.isBlank()) {
            return nuevaLinea;
        }
        return historialActual + "\n" + nuevaLinea;
    }

    private String limpiarTexto(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
