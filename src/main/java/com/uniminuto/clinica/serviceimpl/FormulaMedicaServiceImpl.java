package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.service.MedicamentoService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
/** Implementa las operaciones de fórmulas médicas diferenciando la formulación de la dispensación. */
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;

    @Autowired
    private MedicamentoService medicamentoService;

    public FormulaMedicaServiceImpl() {
    }

    public FormulaMedicaServiceImpl(FormulaMedicaRepository formulaMedicaRepository,
                                    MascotaRepository mascotaRepository,
                                    CitaRepository citaRepository,
                                    MedicoRepository medicoRepository,
                                    MedicamentoRepository medicamentoRepository,
                                    MovimientoMedicamentoRepository movimientoMedicamentoRepository,
                                    MedicamentoService medicamentoService) {
        this.formulaMedicaRepository = formulaMedicaRepository;
        this.mascotaRepository = mascotaRepository;
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.movimientoMedicamentoRepository = movimientoMedicamentoRepository;
        this.medicamentoService = medicamentoService;
    }

    @Override
    @Transactional
    public MiRespuestaRS crearFormulaMedica(FormulaMedicaRq formulaRq) {
        validarFormula(formulaRq);
        if (formulaRq.getMascotaId() == null || formulaRq.getCitaId() == null) {
            throw new BadRequestException("La mascota y la consulta (cita) son obligatorias");
        }

        Mascota mascota = mascotaRepository.findById(formulaRq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("Mascota no encontrada"));
        if (mascota.getCliente() == null || !Boolean.TRUE.equals(mascota.getCliente().getActivo())) {
            throw new BadRequestException("La mascota no pertenece a un cliente registrado y activo");
        }

        Cita cita = citaRepository.findById(formulaRq.getCitaId())
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La consulta seleccionada pertenece a otra mascota");
        }
        if (Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new BadRequestException("No se puede generar una fórmula para una cita cancelada");
        }

        if (formulaMedicaRepository.existsByCita_IdAndMedicamento_IdAndActivoTrue(
                formulaRq.getCitaId(), formulaRq.getMedicamentoId())) {
            throw new ConflictException("El medicamento ya está formulado activamente para esta cita");
        }

        FormulaMedica formula = new FormulaMedica();
        formula.setMascota(mascota);
        formula.setCita(cita);
        asignarDatos(formula, formulaRq);

        // En la formulación se valida existencia suficiente en inventario, pero NO se descuenta stock.
        int disponible = formula.getMedicamento().getCantidadDisponible() != null
                ? formula.getMedicamento().getCantidadDisponible() : 0;
        if (disponible < formula.getCantidadSolicitada()) {
            throw new BadRequestException("Inventario insuficiente para formular " + formula.getCantidadSolicitada()
                    + " unidad(es) de " + formula.getMedicamento().getNombre()
                    + " (disponible en inventario: " + disponible + ")");
        }

        formula.setCantidadEntregada(0);
        formula.setEstado(Constantes.FORMULA_FORMULADA);
        formula.setActivo(true);
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.CREATED.value(), "Fórmula médica creada correctamente (pendiente de dispensación)");
    }

    @Override
    public List<FormulaMedica> listarFormulasMedicas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    public List<FormulaMedica> listarFormulasPorCita(Long citaId) {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita es obligatorio");
        }
        if (!citaRepository.existsById(citaId)) {
            throw new NotFoundException("Cita no encontrada");
        }
        return formulaMedicaRepository.findByCita_IdOrderByFechaCreacionRegistroDesc(citaId);
    }

    @Override
    public List<FormulaMedica> listarFormulasPorMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new BadRequestException("El ID de la mascota es obligatorio");
        }
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("Mascota no encontrada");
        }
        return formulaMedicaRepository.findByMascota_MascotaIdOrderByFechaCreacionRegistroDesc(mascotaId);
    }

    @Override
    public FormulaMedica consultarFormulaMedica(Long formulaId) {
        if (formulaId == null) {
            throw new BadRequestException("El ID de la fórmula no puede ser nulo");
        }
        return buscarFormula(formulaId);
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarFormulaMedica(FormulaMedicaRq formulaRq) {
        validarFormula(formulaRq);
        if (formulaRq.getFormulaId() == null) {
            throw new BadRequestException("El ID de la fórmula es obligatorio para actualizar");
        }

        FormulaMedica formula = buscarFormula(formulaRq.getFormulaId());
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new ConflictException("La fórmula está inactiva y ya no se puede modificar");
        }
        if (Constantes.FORMULA_ENTREGADA.equalsIgnoreCase(formula.getEstado())) {
            throw new ConflictException("La fórmula ya fue dispensada en su totalidad y no se puede modificar");
        }
        if (formulaMedicaRepository.existsByCita_IdAndMedicamento_IdAndActivoTrueAndIdNot(
                formula.getCita().getId(), formulaRq.getMedicamentoId(), formula.getId())) {
            throw new ConflictException("El medicamento ya está formulado para esta cita");
        }

        int entregadaActual = formula.getCantidadEntregada() != null ? formula.getCantidadEntregada() : 0;
        if (entregadaActual > 0 && !formula.getMedicamento().getId().equals(formulaRq.getMedicamentoId())) {
            throw new ConflictException("No se puede cambiar el medicamento de una fórmula que ya tiene entregas parciales");
        }

        asignarDatos(formula, formulaRq);
        if (formula.getCantidadSolicitada() < entregadaActual) {
            throw new BadRequestException("La cantidad solicitada no puede ser menor a la ya dispensada (" + entregadaActual + ")");
        }
        int cantidadPendiente = formula.getCantidadSolicitada() - entregadaActual;
        int disponible = formula.getMedicamento().getCantidadDisponible() != null
                ? formula.getMedicamento().getCantidadDisponible() : 0;
        if (disponible < cantidadPendiente) {
            throw new BadRequestException("Inventario insuficiente para la cantidad pendiente por dispensar ("
                    + cantidadPendiente + " unidad(es), disponible: " + disponible + ")");
        }

        if (entregadaActual > 0) {
            formula.setEstado(formula.getCantidadSolicitada().equals(entregadaActual)
                    ? Constantes.FORMULA_ENTREGADA
                    : Constantes.FORMULA_PARCIAL);
        } else {
            formula.setEstado(Constantes.FORMULA_FORMULADA);
        }

        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.OK.value(), "Fórmula médica actualizada correctamente");
    }

    @Override
    public MiRespuestaRS inactivarFormulaMedica(Long formulaId) {
        if (formulaId == null) {
            throw new BadRequestException("El ID de la fórmula es obligatorio para inactivar");
        }

        FormulaMedica formula = buscarFormula(formulaId);
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new ConflictException("La fórmula ya se encuentra inactiva");
        }
        if (formula.getCantidadEntregada() != null && formula.getCantidadEntregada() > 0) {
            throw new ConflictException("No se puede inactivar una fórmula que ya tiene medicamentos dispensados");
        }

        formula.setActivo(false);
        formula.setEstado(Constantes.FORMULA_CANCELADA);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.OK.value(), "Fórmula médica inactivada correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS dispensarFormula(Long formulaId, Integer cantidad, String observacion) {
        return dispensarFormula(formulaId, cantidad, observacion, null);
    }

    @Override
    @Transactional
    public MiRespuestaRS dispensarFormula(Long formulaId, Integer cantidad, String observacion, String usuario) {
        if (formulaId == null || formulaId <= 0) {
            throw new BadRequestException("El identificador de la fórmula es obligatorio");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new BadRequestException("La cantidad a dispensar debe ser mayor que cero");
        }

        FormulaMedica formula = buscarFormula(formulaId);
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new ConflictException("No se puede dispensar una fórmula inactiva");
        }

        int entregadaActual = formula.getCantidadEntregada() != null ? formula.getCantidadEntregada() : 0;
        int restante = formula.getCantidadSolicitada() - entregadaActual;
        if (restante <= 0) {
            throw new ConflictException("La fórmula ya fue dispensada en su totalidad");
        }
        if (cantidad > restante) {
            throw new BadRequestException("La cantidad a dispensar (" + cantidad
                    + ") supera el saldo pendiente de la fórmula (" + restante + ")");
        }

        Medicamento medicamento = formula.getMedicamento();
        if (medicamento.getCantidadDisponible() < cantidad) {
            throw new BadRequestException("Inventario insuficiente para la dispensación solicitada (disponible: "
                    + medicamento.getCantidadDisponible() + ")");
        }

        String motivo = observacion == null || observacion.isBlank()
                ? "DISPENSACION FORMULA #" + formula.getId()
                : "DISPENSACION FORMULA #" + formula.getId() + ": " + observacion.trim();
        String usuarioResponsable = (usuario != null && !usuario.isBlank())
                ? usuario.trim()
                : (formula.getMedico() != null
                ? "Dr(a). " + formula.getMedico().getNombres() + " " + formula.getMedico().getApellidos()
                : "DESPACHO");

        // Al confirmar la dispensación se descuenta del inventario y se registra el movimiento de salida.
        medicamentoService.registrarSalidaInventario(
                medicamento.getId(), cantidad, motivo, formula.getId(), "FORMULA", usuarioResponsable);

        int nuevaEntregada = entregadaActual + cantidad;
        formula.setCantidadEntregada(nuevaEntregada);
        formula.setFechaDispenacion(LocalDateTime.now());
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formula.setEstado(nuevaEntregada >= formula.getCantidadSolicitada()
                ? Constantes.FORMULA_ENTREGADA
                : Constantes.FORMULA_PARCIAL);
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.OK.value(),
                "Dispensación registrada correctamente (" + nuevaEntregada + "/" + formula.getCantidadSolicitada() + " unidades entregadas)");
    }

    @Override
    public List<MovimientoMedicamento> consultarHistorialPorFormula(Long formulaId) {
        if (formulaId == null || formulaId <= 0) {
            throw new BadRequestException("El identificador de la fórmula es obligatorio");
        }
        FormulaMedica formula = buscarFormula(formulaId);
        return movimientoMedicamentoRepository
                .findByMedicamentoIdAndReferenciaTipoAndReferenciaIdOrderByFechaRegistroDesc(
                        formula.getMedicamento().getId(), "FORMULA", formulaId);
    }

    private void asignarDatos(FormulaMedica formula, FormulaMedicaRq formulaRq) {
        Medico medico = medicoRepository.findById(formulaRq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("Veterinario no encontrado"));
        Medicamento medicamento = medicamentoRepository.findById(formulaRq.getMedicamentoId())
                .orElseThrow(() -> new NotFoundException("Medicamento no encontrado"));

        if (!Boolean.TRUE.equals(medicamento.getActivo())) {
            throw new BadRequestException("El medicamento seleccionado se encuentra inactivo en el inventario");
        }
        if (medicamento.getFechaVence() != null && medicamento.getFechaVence().isBefore(LocalDate.now())) {
            throw new BadRequestException("El medicamento seleccionado está vencido (" + medicamento.getFechaVence() + ") y no puede formularse");
        }
        if (formulaRq.getFechaEmision().isBefore(formula.getCita().getFechaHora().toLocalDate())) {
            throw new BadRequestException("La fecha de emisión no puede ser anterior a la fecha de la consulta");
        }

        formula.setMedico(medico);
        formula.setMedicamento(medicamento);
        formula.setFechaEmision(formulaRq.getFechaEmision());
        formula.setCantidadSolicitada(formulaRq.getCantidadSolicitada());
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setFrecuencia(formulaRq.getFrecuencia().trim());
        formula.setDuracionDias(formulaRq.getDuracionDias());
        formula.setIndicaciones(formulaRq.getIndicaciones().trim());
    }

    private FormulaMedica buscarFormula(Long formulaId) {
        return formulaMedicaRepository.findById(formulaId)
                .orElseThrow(() -> new NotFoundException("Fórmula médica no encontrada"));
    }

    private void validarFormula(FormulaMedicaRq formulaRq) {
        if (formulaRq == null || formulaRq.getMedicoId() == null || formulaRq.getMedicamentoId() == null
                || formulaRq.getFechaEmision() == null
                || formulaRq.getCantidadSolicitada() == null || formulaRq.getCantidadSolicitada() <= 0
                || formulaRq.getDosis() == null || formulaRq.getDosis().isBlank()
                || formulaRq.getFrecuencia() == null || formulaRq.getFrecuencia().isBlank()
                || formulaRq.getDuracionDias() == null
                || formulaRq.getIndicaciones() == null || formulaRq.getIndicaciones().isBlank()) {
            throw new BadRequestException(
                    "Veterinario, medicamento, fecha, cantidad solicitada, dosis, frecuencia, duración e indicaciones son obligatorios");
        }

        if (!formulaRq.getDosis().matches(".*\\d.*") || formulaRq.getDosis().trim().length() > 100) {
            throw new BadRequestException("La dosis debe indicar una cantidad, por ejemplo: 500 mg o 1 tableta");
        }
        if (formulaRq.getDuracionDias() < 1 || formulaRq.getDuracionDias() > Constantes.MAXIMO_DIAS_TRATAMIENTO) {
            throw new BadRequestException("La duración del tratamiento debe estar entre 1 y "
                    + Constantes.MAXIMO_DIAS_TRATAMIENTO + " días");
        }
        if (formulaRq.getFrecuencia().trim().length() > 100) {
            throw new BadRequestException("La frecuencia no puede superar los 100 caracteres");
        }
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
