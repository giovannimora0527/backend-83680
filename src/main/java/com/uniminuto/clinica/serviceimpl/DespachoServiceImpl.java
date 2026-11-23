package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Despacho;
import com.uniminuto.clinica.entity.DespachoDetalle;
import com.uniminuto.clinica.entity.DespachoEstadoLog;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FormulaDetalle;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.MedicamentoLote;
import com.uniminuto.clinica.entity.MovimientoInventario;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ConfirmarDespachoRq;
import com.uniminuto.clinica.models.DespachoFormulaRs;
import com.uniminuto.clinica.models.DespachoItemRs;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.LoteRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.DespachoDetalleRepository;
import com.uniminuto.clinica.repository.DespachoEstadoLogRepository;
import com.uniminuto.clinica.repository.DespachoRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.FormulaDetalleRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MedicamentoLoteRepository;
import com.uniminuto.clinica.repository.MovimientoInventarioRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.DespachoService;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.util.PdfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementacion del despacho de medicamentos.
 *
 * Reglas: el inventario solo se descuenta al confirmar la entrega; no se dispensan
 * lotes vencidos; no se entrega mas de lo disponible ni mas de lo prescrito; se
 * priorizan los lotes que vencen primero (FEFO); cada salida queda relacionada con
 * el despacho y la formula; toda la operacion es una sola transaccion.
 */
@Service
public class DespachoServiceImpl implements DespachoService {

    public static final String PENDIENTE_DESPACHO = "PENDIENTE_DESPACHO";
    public static final String EN_PREPARACION = "EN_PREPARACION";
    public static final String DESPACHO_PARCIAL = "DESPACHO_PARCIAL";
    public static final String DESPACHADO = "DESPACHADO";
    public static final String PENDIENTE_DISPONIBILIDAD = "PENDIENTE_DISPONIBILIDAD";
    public static final String CANCELADO = "CANCELADO";

    /** Estados en los que la formula todavia se puede despachar. */
    private static final List<String> PENDIENTES =
            List.of(PENDIENTE_DESPACHO, EN_PREPARACION, DESPACHO_PARCIAL, PENDIENTE_DISPONIBILIDAD);

    @Autowired
    private FormulaMedicaRepository formulaRepository;
    @Autowired
    private FormulaDetalleRepository formulaDetalleRepository;
    @Autowired
    private FormulaMedicaService formulaService;
    @Autowired
    private MedicamentoLoteRepository loteRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;
    @Autowired
    private DespachoRepository despachoRepository;
    @Autowired
    private DespachoDetalleRepository despachoDetalleRepository;
    @Autowired
    private DespachoEstadoLogRepository logRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private InventarioService inventarioService;

    // ------------------------------------------------------------------ consultas

    @Override
    public List<FormulaMedicaRs> listarPendientes(String buscar, LocalDate fecha) {
        String texto = this.normalizar(buscar);
        return formulaRepository.findByActivoTrueAndEstadoDespachoInOrderByFechaCreacionRegistroAsc(PENDIENTES).stream()
                .map(f -> formulaService.convertir(f, false))
                .filter(f -> fecha == null || fecha.equals(f.getFechaEmision()))
                .filter(f -> texto.isEmpty() || String.valueOf(f.getId()).equals(texto)
                        || this.normalizar(f.getClienteNombre()).contains(texto)
                        || this.normalizar(f.getMascotaNombre()).contains(texto))
                .toList();
    }

    @Override
    public DespachoFormulaRs consultarFormula(Long formulaId) {
        FormulaMedica formula = this.buscarFormula(formulaId);
        DespachoFormulaRs rs = new DespachoFormulaRs();
        rs.setFormula(formulaService.convertir(formula, true));
        List<DespachoItemRs> items = new ArrayList<>();
        for (FormulaDetalle d : formulaDetalleRepository.findByFormulaIdOrderByIdAsc(formulaId)) {
            items.add(this.armarItem(d));
        }
        rs.setItems(items);
        rs.setDespachos(despachoRepository.findByFormulaIdOrderByFechaDesc(formulaId).stream().map(this::convertirDespacho).toList());
        Map<Long, String> usuarios = this.usuarios();
        rs.setLog(despachoRepository.findLog(formulaId).stream().map(l -> {
            DespachoFormulaRs.Log log = new DespachoFormulaRs.Log();
            log.setEstadoAnterior(l.getEstadoAnterior());
            log.setEstadoNuevo(l.getEstadoNuevo());
            log.setUsuario(usuarios.get(l.getUsuarioId()));
            log.setFecha(l.getFecha());
            log.setObservacion(l.getObservacion());
            return log;
        }).toList());
        return rs;
    }

    @Override
    public List<DespachoRs> historial(LocalDate desde, LocalDate hasta) {
        LocalDate inicio = desde == null ? LocalDate.now().minusDays(30) : desde;
        LocalDate fin = hasta == null ? LocalDate.now() : hasta;
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha inicial no puede ser mayor que la final");
        }
        return despachoRepository.findByFechaBetweenOrderByFechaDesc(inicio.atStartOfDay(), fin.atTime(23, 59, 59))
                .stream().map(this::convertirDespacho).toList();
    }

    // ------------------------------------------------------------------ operaciones

    @Override
    @Transactional
    public MiRespuestaRS iniciarPreparacion(Long formulaId) {
        FormulaMedica formula = this.buscarPendiente(formulaId);
        this.cambiarEstado(formula, EN_PREPARACION, "Inicio de verificación de medicamentos");
        return this.exito("La fórmula quedó en preparación");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiRespuestaRS confirmarDespacho(ConfirmarDespachoRq rq) {
        if (rq == null || rq.getFormulaId() == null) {
            throw new BadRequestException("Seleccione la fórmula a despachar");
        }
        FormulaMedica formula = this.buscarPendiente(rq.getFormulaId());
        Long usuarioId = UsuarioActual.get().getUsuarioId();

        // Paso 1. Crear el despacho (si algo falla, la transaccion deshace todo)
        Despacho despacho = new Despacho();
        despacho.setFormulaId(formula.getId());
        despacho.setEstado(Despacho.CONFIRMADO);
        despacho.setUsuarioId(usuarioId);
        despacho.setFecha(LocalDateTime.now());
        despacho.setObservacion(rq.getObservacion());
        despachoRepository.save(despacho);

        int totalEntregado = 0;
        for (ConfirmarDespachoRq.Item item : rq.getItems()) {
            if (item.getCantidad() == null || item.getCantidad() == 0) {
                continue;
            }
            // Paso 2. Validar contra lo prescrito (el despacho no cambia dosis ni cantidades del medico)
            FormulaDetalle detalle = formulaDetalleRepository.findById(item.getFormulaDetalleId() == null ? -1 : item.getFormulaDetalleId())
                    .filter(d -> d.getFormulaId().equals(formula.getId()))
                    .orElseThrow(() -> new BadRequestException("El medicamento no pertenece a la fórmula"));
            String nombre = detalle.getMedicamento().getNombre();
            int pendiente = detalle.getCantidad() - detalle.getCantidadEntregada();
            if (item.getCantidad() < 0) {
                throw new BadRequestException(nombre + ": la cantidad no puede ser negativa");
            }
            if (item.getCantidad() > pendiente) {
                throw new BadRequestException(nombre + ": no puede entregar " + item.getCantidad()
                        + " unidades, solo quedan " + pendiente + " pendientes (no se descuenta dos veces)");
            }

            // Paso 3. Elegir lotes: los indicados o automatico por vencimiento (FEFO)
            List<ConfirmarDespachoRq.LoteCantidad> lotes = item.getLotes() == null || item.getLotes().isEmpty()
                    ? this.sugerirLotes(detalle.getMedicamento().getId(), item.getCantidad())
                    : item.getLotes();
            int suma = lotes.stream().mapToInt(l -> l.getCantidad() == null ? 0 : l.getCantidad()).sum();
            if (suma < item.getCantidad()) {
                throw new BadRequestException(nombre + ": no hay existencias vigentes suficientes. "
                        + "Disponible: " + inventarioService.stockDisponible(detalle.getMedicamento().getId())
                        + ". Puede registrar una entrega parcial.");
            }
            if (suma != item.getCantidad()) {
                throw new BadRequestException(nombre + ": la suma de los lotes no coincide con la cantidad a entregar");
            }
            this.validarPrioridadFefo(detalle, lotes);

            // Paso 4. Descontar cada lote (bloqueado) y registrar la salida
            for (ConfirmarDespachoRq.LoteCantidad lc : lotes) {
                if (lc.getCantidad() == null || lc.getCantidad() <= 0) {
                    continue;
                }
                MedicamentoLote lote = loteRepository.findBloqueado(lc.getLoteId())
                        .orElseThrow(() -> new BadRequestException("Lote no encontrado"));
                if (!lote.getMedicamento().getId().equals(detalle.getMedicamento().getId())) {
                    throw new BadRequestException("El lote " + lote.getNumeroLote() + " no es de " + nombre);
                }
                if (lote.estaVencido()) {
                    throw new BadRequestException("No se permite dispensar el lote vencido " + lote.getNumeroLote());
                }
                if (lc.getCantidad() > lote.getCantidadDisponible()) {
                    throw new BadRequestException("El lote " + lote.getNumeroLote() + " solo tiene "
                            + lote.getCantidadDisponible() + " unidades");
                }
                lote.setCantidadDisponible(lote.getCantidadDisponible() - lc.getCantidad());
                loteRepository.save(lote);

                DespachoDetalle dd = new DespachoDetalle();
                dd.setDespachoId(despacho.getId());
                dd.setFormulaDetalleId(detalle.getId());
                dd.setLote(lote);
                dd.setCantidad(lc.getCantidad());
                despachoDetalleRepository.save(dd);

                MovimientoInventario mov = new MovimientoInventario();
                mov.setMedicamentoId(detalle.getMedicamento().getId());
                mov.setLoteId(lote.getId());
                mov.setTipo(MovimientoInventario.SALIDA);
                mov.setCantidad(lc.getCantidad());
                mov.setSaldoLote(lote.getCantidadDisponible());
                mov.setFecha(LocalDateTime.now());
                mov.setUsuarioId(usuarioId);
                mov.setFormulaId(formula.getId());
                mov.setDespachoId(despacho.getId());
                mov.setObservacion("Dispensación fórmula #" + formula.getId());
                movimientoRepository.save(mov);
            }
            detalle.setCantidadEntregada(detalle.getCantidadEntregada() + item.getCantidad());
            formulaDetalleRepository.save(detalle);
            totalEntregado += item.getCantidad();
        }
        if (totalEntregado == 0) {
            throw new BadRequestException("Indique al menos una cantidad a entregar");
        }

        // Paso 5. Nuevo estado: despachado o parcial
        boolean completo = formulaDetalleRepository.findByFormulaIdOrderByIdAsc(formula.getId()).stream()
                .allMatch(d -> d.getCantidadEntregada().equals(d.getCantidad()));
        String nuevoEstado = completo ? DESPACHADO : DESPACHO_PARCIAL;
        this.cambiarEstado(formula, nuevoEstado, "Despacho #" + despacho.getId() + ": " + totalEntregado + " unidades entregadas");
        return this.exito(completo ? "Despacho confirmado. Se entregaron todos los medicamentos."
                : "Despacho parcial confirmado. Quedan medicamentos pendientes por entregar.");
    }

    @Override
    @Transactional
    public MiRespuestaRS marcarSinDisponibilidad(Long formulaId, String observacion) {
        FormulaMedica formula = this.buscarPendiente(formulaId);
        boolean faltaInventario = formulaDetalleRepository.findByFormulaIdOrderByIdAsc(formulaId).stream()
                .anyMatch(d -> d.getCantidad() - d.getCantidadEntregada() > inventarioService.stockDisponible(d.getMedicamento().getId()));
        if (!faltaInventario) {
            throw new BadRequestException("Hay existencias suficientes; puede confirmar el despacho");
        }
        this.cambiarEstado(formula, PENDIENTE_DISPONIBILIDAD,
                observacion == null || observacion.isBlank() ? "Medicamentos sin existencias suficientes" : observacion);
        return this.exito("La fórmula quedó pendiente por disponibilidad");
    }

    @Override
    @Transactional
    public MiRespuestaRS cancelarDespacho(Long formulaId, String motivo) {
        if (motivo == null || motivo.trim().length() < 10) {
            throw new BadRequestException("Escriba el motivo de la cancelación (mínimo 10 caracteres)");
        }
        FormulaMedica formula = this.buscarPendiente(formulaId);
        this.cambiarEstado(formula, CANCELADO, "Cancelado: " + motivo.trim());
        return this.exito("Despacho cancelado. Lo que ya se entregó sigue registrado.");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiRespuestaRS revertirDespacho(Long despachoId, String motivo) {
        if (motivo == null || motivo.trim().length() < 10) {
            throw new BadRequestException("Escriba el motivo de la reversión (mínimo 10 caracteres)");
        }
        Despacho despacho = despachoRepository.findById(despachoId == null ? -1 : despachoId)
                .orElseThrow(() -> new BadRequestException("Despacho no encontrado", HttpStatus.NOT_FOUND));
        if (!Despacho.CONFIRMADO.equals(despacho.getEstado())) {
            throw new BadRequestException("El despacho ya fue revertido");
        }
        FormulaMedica formula = this.buscarFormula(despacho.getFormulaId());
        if (facturaRepository.existsByCitaIdAndEstadoNot(formula.getCita().getId(), Factura.ANULADA)) {
            throw new BadRequestException("La atención ya fue facturada. Anule la factura antes de revertir el despacho.");
        }
        Long usuarioId = UsuarioActual.get().getUsuarioId();
        // Devolver cada linea al lote del que salio y dejar el movimiento de reversion
        for (DespachoDetalle dd : despachoRepository.findDetalles(despacho.getId())) {
            MedicamentoLote lote = loteRepository.findBloqueado(dd.getLote().getId()).orElseThrow();
            lote.setCantidadDisponible(lote.getCantidadDisponible() + dd.getCantidad());
            loteRepository.save(lote);
            FormulaDetalle detalle = formulaDetalleRepository.findById(dd.getFormulaDetalleId()).orElseThrow();
            detalle.setCantidadEntregada(detalle.getCantidadEntregada() - dd.getCantidad());
            formulaDetalleRepository.save(detalle);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setMedicamentoId(lote.getMedicamento().getId());
            mov.setLoteId(lote.getId());
            mov.setTipo(MovimientoInventario.REVERSION);
            mov.setCantidad(dd.getCantidad());
            mov.setSaldoLote(lote.getCantidadDisponible());
            mov.setFecha(LocalDateTime.now());
            mov.setUsuarioId(usuarioId);
            mov.setFormulaId(formula.getId());
            mov.setDespachoId(despacho.getId());
            mov.setObservacion("Reversión despacho #" + despacho.getId() + ": " + motivo.trim());
            movimientoRepository.save(mov);
        }
        despacho.setEstado(Despacho.REVERTIDO);
        despacho.setUsuarioReversionId(usuarioId);
        despacho.setFechaReversion(LocalDateTime.now());
        despacho.setMotivoReversion(motivo.trim());
        despachoRepository.save(despacho);

        boolean algoEntregado = formulaDetalleRepository.findByFormulaIdOrderByIdAsc(formula.getId()).stream()
                .anyMatch(d -> d.getCantidadEntregada() > 0);
        formula.setActivo(true);
        this.cambiarEstado(formula, algoEntregado ? DESPACHO_PARCIAL : PENDIENTE_DESPACHO,
                "Reversión del despacho #" + despacho.getId() + ": " + motivo.trim());
        return this.exito("Despacho revertido. Las unidades volvieron al inventario.");
    }

    @Override
    public byte[] comprobantePdf(Long despachoId) {
        Despacho despacho = despachoRepository.findById(despachoId == null ? -1 : despachoId)
                .orElseThrow(() -> new BadRequestException("Despacho no encontrado", HttpStatus.NOT_FOUND));
        DespachoRs d = this.convertirDespacho(despacho);
        FormulaMedicaRs f = formulaService.convertir(this.buscarFormula(despacho.getFormulaId()), false);
        DateTimeFormatter fh = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<List<String>> filas = d.getLineas().stream().map(l -> List.of(l.getMedicamento(), l.getNumeroLote(),
                l.getFechaVence() == null ? "No aplica" : l.getFechaVence().toString(), String.valueOf(l.getCantidad()))).toList();
        PdfUtil.Documento doc = new PdfUtil.Documento("Comprobante de despacho N.° " + d.getId())
                .seccion("Datos del despacho", List.of(
                        new String[]{"Fórmula", "#" + f.getId() + " del " + f.getFechaEmision()},
                        new String[]{"Fecha de entrega", d.getFecha().format(fh)},
                        new String[]{"Propietario", f.getClienteNombre()},
                        new String[]{"Mascota", f.getMascotaNombre()},
                        new String[]{"Médico que formuló", f.getMedicoNombre()},
                        new String[]{"Entregado por", d.getUsuario()},
                        new String[]{"Estado del despacho", d.getEstado()},
                        new String[]{"Estado de la fórmula", f.getEstadoDespacho()}))
                .tabla("Medicamentos entregados", List.of("Medicamento", "Lote", "Vence", "Cantidad"), filas)
                .texto("Observación", d.getObservacion());
        if (Despacho.REVERTIDO.equals(d.getEstado())) {
            doc.texto("Reversión", "Revertido el " + d.getFechaReversion().format(fh) + " por " + d.getUsuarioReversion()
                    + ". Motivo: " + d.getMotivoReversion());
        }
        return doc.texto(null, "\n\n_______________________________\nRecibí conforme (propietario)").generar();
    }

    // ------------------------------------------------------------------ ayudas

    /**
     * Arma el item de despacho de un medicamento de la formula.
     */
    private DespachoItemRs armarItem(FormulaDetalle d) {
        DespachoItemRs item = new DespachoItemRs();
        item.setFormulaDetalleId(d.getId());
        item.setMedicamentoId(d.getMedicamento().getId());
        item.setMedicamentoCodigo(d.getMedicamento().getCodigo());
        item.setMedicamentoNombre(d.getMedicamento().getNombre());
        item.setDosis(d.getDosis());
        item.setFrecuencia(d.getFrecuencia());
        item.setDuracionDias(d.getDuracionDias());
        item.setViaAdministracion(d.getViaAdministracion());
        item.setIndicaciones(d.getIndicaciones());
        item.setCantidadPrescrita(d.getCantidad());
        item.setCantidadEntregada(d.getCantidadEntregada());
        int pendiente = d.getCantidad() - d.getCantidadEntregada();
        item.setCantidadPendiente(pendiente);
        for (MedicamentoLote lote : loteRepository.findLotesDeMedicamento(d.getMedicamento().getId())) {
            LoteRs lr = inventarioService.convertirLote(lote);
            if (lote.estaVencido() && lote.getCantidadDisponible() > 0) {
                item.getLotesVencidos().add(lr);
            } else if (!lote.estaVencido() && lote.getCantidadDisponible() > 0) {
                item.getLotesVigentes().add(lr);
            }
        }
        int disponible = item.getLotesVigentes().stream().mapToInt(LoteRs::getCantidadDisponible).sum();
        item.setStockDisponible(disponible);
        item.setPuedeEntregar(Math.min(pendiente, disponible));
        item.setSugerencia(this.sugerirLotes(d.getMedicamento().getId(), item.getPuedeEntregar()));
        if (pendiente == 0) {
            item.setAlerta("Entregado completo");
        } else if (disponible == 0) {
            item.setAlerta("Sin existencias vigentes: no se puede entregar");
        } else if (disponible < pendiente) {
            item.setAlerta("Existencias insuficientes: solo se pueden entregar " + disponible + " (entrega parcial)");
        }
        return item;
    }

    /**
     * Reparte una cantidad entre los lotes vigentes, del que vence primero al ultimo (FEFO).
     */
    private List<ConfirmarDespachoRq.LoteCantidad> sugerirLotes(Long medicamentoId, int cantidad) {
        List<ConfirmarDespachoRq.LoteCantidad> sugerencia = new ArrayList<>();
        int restante = cantidad;
        for (MedicamentoLote lote : loteRepository.findLotesVigentes(medicamentoId, LocalDate.now())) {
            if (restante <= 0) {
                break;
            }
            int tomar = Math.min(restante, lote.getCantidadDisponible());
            ConfirmarDespachoRq.LoteCantidad lc = new ConfirmarDespachoRq.LoteCantidad();
            lc.setLoteId(lote.getId());
            lc.setCantidad(tomar);
            sugerencia.add(lc);
            restante -= tomar;
        }
        return sugerencia;
    }

    /**
     * Si se eligen lotes a mano, no se puede saltar un lote vigente que vence antes
     * y que todavia tiene unidades sin usar.
     */
    private void validarPrioridadFefo(FormulaDetalle detalle, List<ConfirmarDespachoRq.LoteCantidad> elegidos) {
        Map<Long, Integer> usados = new HashMap<>();
        elegidos.forEach(l -> usados.merge(l.getLoteId(), l.getCantidad() == null ? 0 : l.getCantidad(), Integer::sum));
        List<MedicamentoLote> vigentes = loteRepository.findLotesVigentes(detalle.getMedicamento().getId(), LocalDate.now());
        boolean hayHueco = false;
        for (MedicamentoLote lote : vigentes) {
            int usado = usados.getOrDefault(lote.getId(), 0);
            if (hayHueco && usado > 0) {
                throw new BadRequestException(detalle.getMedicamento().getNombre()
                        + ": debe despachar primero de los lotes que vencen antes (FEFO)");
            }
            if (usado < lote.getCantidadDisponible()) {
                hayHueco = true;
            }
        }
    }

    /**
     * Cambia el estado de la formula y deja el registro en el log.
     */
    private void cambiarEstado(FormulaMedica formula, String nuevo, String observacion) {
        DespachoEstadoLog log = new DespachoEstadoLog();
        log.setFormulaId(formula.getId());
        log.setEstadoAnterior(formula.getEstadoDespacho());
        log.setEstadoNuevo(nuevo);
        log.setUsuarioId(UsuarioActual.get().getUsuarioId());
        log.setFecha(LocalDateTime.now());
        log.setObservacion(observacion);
        logRepository.save(log);
        formula.setEstadoDespacho(nuevo);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaRepository.save(formula);
    }

    /**
     * Convierte un despacho para el historial.
     */
    private DespachoRs convertirDespacho(Despacho despacho) {
        Map<Long, String> usuarios = this.usuarios();
        FormulaMedica formula = this.buscarFormula(despacho.getFormulaId());
        DespachoRs rs = new DespachoRs();
        rs.setId(despacho.getId());
        rs.setFormulaId(despacho.getFormulaId());
        rs.setFecha(despacho.getFecha());
        rs.setUsuario(usuarios.get(despacho.getUsuarioId()));
        rs.setEstado(despacho.getEstado());
        rs.setObservacion(despacho.getObservacion());
        rs.setMascota(formula.getCita().getMascota().getNombreMascota());
        rs.setPropietario(formula.getCita().getCliente().getNombres() + " " + formula.getCita().getCliente().getApellidos());
        if (formula.getMedico() != null) {
            rs.setMedico(formula.getMedico().getNombres() + " " + formula.getMedico().getApellidos());
        }
        rs.setEstadoFormula(formula.getEstadoDespacho());
        rs.setUsuarioReversion(usuarios.get(despacho.getUsuarioReversionId()));
        rs.setFechaReversion(despacho.getFechaReversion());
        rs.setMotivoReversion(despacho.getMotivoReversion());
        for (DespachoDetalle dd : despachoRepository.findDetalles(despacho.getId())) {
            DespachoRs.Linea linea = new DespachoRs.Linea();
            linea.setMedicamento(dd.getLote().getMedicamento().getNombre());
            linea.setNumeroLote(dd.getLote().getNumeroLote());
            linea.setFechaVence(dd.getLote().getFechaVence());
            linea.setCantidad(dd.getCantidad());
            rs.getLineas().add(linea);
        }
        return rs;
    }

    private FormulaMedica buscarFormula(Long formulaId) {
        if (formulaId == null) {
            throw new BadRequestException("El id de la fórmula es obligatorio");
        }
        return formulaRepository.findById(formulaId)
                .orElseThrow(() -> new BadRequestException("Fórmula no encontrada", HttpStatus.NOT_FOUND));
    }

    /**
     * Busca la formula y valida que todavia se pueda despachar.
     */
    private FormulaMedica buscarPendiente(Long formulaId) {
        FormulaMedica formula = this.buscarFormula(formulaId);
        if (!Boolean.TRUE.equals(formula.getActivo()) || !PENDIENTES.contains(formula.getEstadoDespacho())) {
            throw new BadRequestException("La fórmula no está pendiente de despacho (estado: "
                    + formula.getEstadoDespacho() + ")");
        }
        return formula;
    }

    private Map<Long, String> usuarios() {
        return usuarioRepository.findAll().stream().collect(Collectors.toMap(Usuario::getId, Usuario::getUsername));
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private MiRespuestaRS exito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
