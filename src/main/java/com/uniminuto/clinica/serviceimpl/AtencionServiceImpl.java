package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaDetalle;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AtencionInfoRs;
import com.uniminuto.clinica.models.AtencionRq;
import com.uniminuto.clinica.models.AtencionRs;
import com.uniminuto.clinica.models.FormulaDetalleRs;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaDetalleRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.TarifaRepository;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.AtencionService;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.service.TarifaService;
import com.uniminuto.clinica.util.PdfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementacion de la atencion medica.
 */
@Service
public class AtencionServiceImpl implements AtencionService {

    /** Vias de administracion permitidas. */
    private static final List<String> VIAS = List.of("Oral", "Tópica", "Oftálmica", "Ótica", "Subcutánea",
            "Intramuscular", "Intravenosa", "Inhalada", "Rectal", "Otra");

    @Autowired
    private CitaService citaService;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private HistoriaMedicaRepository historiaRepository;
    @Autowired
    private HistoriaMedicaService historiaService;
    @Autowired
    private FormulaMedicaRepository formulaRepository;
    @Autowired
    private FormulaDetalleRepository detalleRepository;
    @Autowired
    private FormulaMedicaService formulaService;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private TarifaRepository tarifaRepository;
    @Autowired
    private TarifaService tarifaService;
    @Autowired
    private InventarioService inventarioService;

    @Override
    public AtencionInfoRs consultarAtencion(Long citaId) {
        Cita cita = this.citaPropia(citaId);
        Medico medico = cita.getMedico();
        AtencionInfoRs rs = new AtencionInfoRs();
        rs.setCita(citaService.convertirCita(cita));
        rs.setEspecialidad(medico.getEspecializacion() == null ? null : medico.getEspecializacion().getNombre());
        // Antecedentes: historias anteriores de la mascota
        rs.setAntecedentes(historiaRepository.findByMascotaMascotaIdOrderByFechaHoraDesc(cita.getMascota().getMascotaId())
                .stream().filter(h -> h.getCita() == null || !h.getCita().getId().equals(cita.getId()))
                .map(historiaService::convertirHistoria).toList());
        rs.setServicios(this.serviciosAplicables(medico));
        historiaRepository.findByCitaId(cita.getId()).stream().findFirst()
                .ifPresent(h -> rs.setHistoria(historiaService.convertirHistoria(h)));
        formulaRepository.findByCitaIdAndActivoTrue(cita.getId()).stream().findFirst()
                .ifPresent(f -> rs.setFormula(formulaService.convertir(f, true)));
        return rs;
    }

    @Override
    @Transactional
    public AtencionRs finalizarAtencion(AtencionRq rq) {
        // Paso 1. Validar la cita
        if (rq == null) {
            throw new BadRequestException("Los datos de la atención son obligatorios");
        }
        Cita cita = this.citaPropia(rq.getCitaId());
        if (cita.getEstado().equals("cancelada")) {
            throw new BadRequestException("No se puede atender una cita cancelada");
        }
        if (historiaRepository.existsByCitaId(cita.getId()) || cita.getEstado().equals("atendida")) {
            throw new BadRequestException("Esta cita ya fue atendida");
        }
        if (cita.getFechaHora().toLocalDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Solo se pueden atender citas del día de hoy o anteriores");
        }

        // Paso 2. Validar los datos clinicos y el servicio
        this.obligatorio(rq.getMotivo(), "El motivo de consulta es obligatorio");
        this.obligatorio(rq.getSintomas(), "Registre los síntomas identificados");
        this.obligatorio(rq.getDiagnostico(), "El diagnóstico es obligatorio");
        if (rq.getTarifaId() == null) {
            throw new BadRequestException("Seleccione el servicio o consulta prestada");
        }
        Tarifa tarifa = tarifaService.buscarActiva(rq.getTarifaId());
        if (this.serviciosAplicables(cita.getMedico()).stream().noneMatch(t -> t.getId().equals(tarifa.getId()))) {
            throw new BadRequestException("El servicio seleccionado no aplica para la especialidad del médico");
        }

        // Paso 3. Validar los medicamentos formulados
        AtencionRs rs = new AtencionRs();
        Set<Long> repetidos = new HashSet<>();
        List<Medicamento> medicamentos = new ArrayList<>();
        for (AtencionRq.Medicamento m : rq.getMedicamentos()) {
            Medicamento medicamento = this.validarMedicamento(m, repetidos);
            medicamentos.add(medicamento);
            int disponible = inventarioService.stockDisponible(medicamento.getId());
            if (disponible < m.getCantidad()) {
                // Se permite formular, pero el despacho podra ser parcial
                rs.getAdvertencias().add(medicamento.getNombre() + ": se formularon " + m.getCantidad()
                        + " y hay " + disponible + " disponibles. El despacho podría ser parcial.");
            }
        }

        // Paso 4. Guardar la historia clinica
        HistoriaMedica historia = new HistoriaMedica();
        historia.setMascota(cita.getMascota());
        historia.setCita(cita);
        historia.setMedico(cita.getMedico());
        historia.setFechaHora(LocalDateTime.now());
        historia.setMotivo(rq.getMotivo().trim());
        historia.setSintomas(rq.getSintomas().trim());
        historia.setDiagnostico(rq.getDiagnostico().trim());
        historia.setObservaciones(rq.getObservaciones());
        historia.setRecomendaciones(rq.getRecomendaciones());
        historia.setTarifa(tarifa);
        historia.setFechaCreacion(LocalDateTime.now());
        historiaRepository.save(historia);
        rs.setHistoriaId(historia.getId());

        // Paso 5. Formula (solo si se formularon medicamentos). No descuenta inventario:
        // el inventario se afecta en el modulo de despacho al entregar.
        if (!medicamentos.isEmpty()) {
            FormulaMedica formula = new FormulaMedica();
            formula.setCita(cita);
            formula.setMedico(cita.getMedico());
            formula.setHistoriaId(historia.getId());
            formula.setFechaEmision(LocalDate.now());
            formula.setIndicaciones(rq.getIndicacionesFormula());
            formula.setActivo(true);
            formula.setEstadoDespacho("PENDIENTE_DESPACHO");
            formula.setFechaCreacionRegistro(LocalDateTime.now());
            formulaRepository.save(formula);
            for (int i = 0; i < medicamentos.size(); i++) {
                AtencionRq.Medicamento m = rq.getMedicamentos().get(i);
                FormulaDetalle detalle = new FormulaDetalle();
                detalle.setFormulaId(formula.getId());
                detalle.setMedicamento(medicamentos.get(i));
                detalle.setDosis(m.getDosis().trim());
                detalle.setFrecuencia(m.getFrecuencia().trim());
                detalle.setDuracionDias(m.getDuracionDias());
                detalle.setCantidad(m.getCantidad());
                detalle.setViaAdministracion(m.getViaAdministracion());
                detalle.setIndicaciones(m.getIndicaciones());
                detalle.setCantidadEntregada(0);
                detalleRepository.save(detalle);
            }
            rs.setFormulaId(formula.getId());
        }

        // Paso 6. La cita queda atendida (finalizada)
        cita.setEstado("atendida");
        citaRepository.save(cita);
        rs.setMessage("Atención finalizada. La información quedó en la historia clínica de "
                + cita.getMascota().getNombreMascota() + ".");
        return rs;
    }

    @Override
    public byte[] prefacturaPdf(Long citaId) {
        Cita cita = this.citaPropia(citaId);
        HistoriaMedica historia = historiaRepository.findByCitaId(cita.getId()).stream().findFirst()
                .orElseThrow(() -> new BadRequestException("La cita aún no ha sido atendida"));
        List<List<String>> filas = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        if (historia.getTarifa() != null) {
            Tarifa t = historia.getTarifa();
            BigDecimal ivaLinea = this.porcentaje(t.getValor(), t.getIvaPorcentaje());
            filas.add(List.of("Servicio", t.getNombre(), "1", PdfUtil.pesos(t.getValor()),
                    t.getIvaPorcentaje() + "%", PdfUtil.pesos(t.getValor().add(ivaLinea))));
            subtotal = subtotal.add(t.getValor());
            iva = iva.add(ivaLinea);
        }
        FormulaMedicaRs formula = formulaRepository.findByCitaIdAndActivoTrue(cita.getId()).stream().findFirst()
                .map(f -> formulaService.convertir(f, true)).orElse(null);
        if (formula != null) {
            for (FormulaDetalleRs d : formula.getDetalles()) {
                Medicamento m = medicamentoRepository.findById(d.getMedicamentoId()).orElseThrow();
                BigDecimal base = m.getPrecioVenta().multiply(BigDecimal.valueOf(d.getCantidad()));
                BigDecimal ivaLinea = this.porcentaje(base, m.getIvaPorcentaje());
                filas.add(List.of("Medicamento", m.getNombre(), String.valueOf(d.getCantidad()),
                        PdfUtil.pesos(m.getPrecioVenta()), m.getIvaPorcentaje() + "%", PdfUtil.pesos(base.add(ivaLinea))));
                subtotal = subtotal.add(base);
                iva = iva.add(ivaLinea);
            }
        }
        DateTimeFormatter fh = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new PdfUtil.Documento("Prefactura - Cita N.° " + cita.getId())
                .seccion("Datos", List.of(
                        new String[]{"Propietario", cita.getCliente().getNombres() + " " + cita.getCliente().getApellidos()},
                        new String[]{"Documento", cita.getCliente().getTipoDocumento() + " " + cita.getCliente().getNumeroDocumento()},
                        new String[]{"Mascota", cita.getMascota().getNombreMascota()},
                        new String[]{"Fecha de atención", historia.getFechaHora().format(fh)},
                        new String[]{"Médico", cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos()},
                        new String[]{"Especialidad", cita.getMedico().getEspecializacion() == null ? "-"
                                : cita.getMedico().getEspecializacion().getNombre()}))
                .tabla("Servicios prestados y medicamentos formulados",
                        List.of("Tipo", "Descripción", "Cant.", "Valor unit.", "IVA", "Total"), filas)
                .totales(List.of(new String[]{"Subtotal", PdfUtil.pesos(subtotal)},
                        new String[]{"IVA", PdfUtil.pesos(iva)},
                        new String[]{"Total estimado", PdfUtil.pesos(subtotal.add(iva))}))
                .texto(null, "Documento informativo, no válido como factura. En la factura final solo se cobran "
                        + "los medicamentos efectivamente entregados en el despacho.")
                .generar();
    }

    // ------------------------------------------------------------------ ayudas

    /**
     * Tarifas activas generales o de la especialidad del medico.
     */
    private List<Tarifa> serviciosAplicables(Medico medico) {
        Long especialidadId = medico.getEspecializacion() == null ? null : medico.getEspecializacion().getId();
        return tarifaRepository.findAllByOrderByTipoAscNombreAsc().stream()
                .filter(t -> Boolean.TRUE.equals(t.getActivo()))
                .filter(t -> t.getEspecializacion() == null || t.getEspecializacion().getId().equals(especialidadId))
                .toList();
    }

    /**
     * Valida un medicamento formulado.
     */
    private Medicamento validarMedicamento(AtencionRq.Medicamento m, Set<Long> repetidos) {
        if (m == null || m.getMedicamentoId() == null) {
            throw new BadRequestException("Seleccione el medicamento");
        }
        Medicamento medicamento = medicamentoRepository.findById(m.getMedicamentoId())
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
        if (!Boolean.TRUE.equals(medicamento.getActivo())) {
            throw new BadRequestException(medicamento.getNombre() + " está inactivo");
        }
        if (!repetidos.add(medicamento.getId())) {
            throw new BadRequestException(medicamento.getNombre() + " está repetido en la fórmula");
        }
        String nombre = medicamento.getNombre() + ": ";
        this.obligatorio(m.getDosis(), nombre + "la dosis es obligatoria");
        this.obligatorio(m.getFrecuencia(), nombre + "la frecuencia es obligatoria");
        if (m.getDuracionDias() == null || m.getDuracionDias() < 1 || m.getDuracionDias() > 365) {
            throw new BadRequestException(nombre + "la duración debe estar entre 1 y 365 días");
        }
        if (m.getCantidad() == null || m.getCantidad() < 1 || m.getCantidad() > 1000) {
            throw new BadRequestException(nombre + "la cantidad debe estar entre 1 y 1000 unidades");
        }
        if (m.getViaAdministracion() == null || !VIAS.contains(m.getViaAdministracion())) {
            throw new BadRequestException(nombre + "seleccione una vía de administración válida");
        }
        return medicamento;
    }

    private void obligatorio(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new BadRequestException(mensaje);
        }
    }

    private BigDecimal porcentaje(BigDecimal base, BigDecimal porcentaje) {
        return base.multiply(porcentaje == null ? BigDecimal.ZERO : porcentaje)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Busca la cita y valida que sea del medico autenticado.
     */
    private Cita citaPropia(Long citaId) {
        Cita cita = citaService.buscarCita(citaId);
        if (!cita.getMedico().getId().equals(UsuarioActual.medicoId())) {
            throw new BadRequestException("Esa cita no está asignada a usted", HttpStatus.FORBIDDEN);
        }
        return cita;
    }
}
