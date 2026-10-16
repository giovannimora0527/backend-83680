package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.service.ClienteService;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio de las fórmulas médicas.
 */
@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    /** Duración máxima permitida de un tratamiento (un año). */
    private static final int MAX_DIAS_TRATAMIENTO = 365;

    /** Repositorio de fórmulas. */
    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    /** Repositorio de medicamentos. */
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    /** Repositorio de médicos. */
    @Autowired
    private MedicoRepository medicoRepository;

    /** Servicio de clientes. */
    @Autowired
    private ClienteService clienteService;

    /** Servicio de mascotas. */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de citas. */
    @Autowired
    private CitaService citaService;

    @Override
    public List<FormulaMedicaRs> listarFormulasMedicas() {
        // Paso 1. Consultar las formulas ya ordenadas por fecha de creacion
        List<FormulaMedica> formulas = formulaMedicaRepository
                .findAllByOrderByFechaCreacionRegistroDesc();

        // Paso 2. Convertir cada formula al objeto de respuesta
        List<FormulaMedicaRs> respuesta = new ArrayList<>();
        for (FormulaMedica formula : formulas) {
            respuesta.add(this.convertirFormula(formula));
        }
        return respuesta;
    }

    @Override
    public FormulaMedicaRs consultarFormula(Long formulaId) {
        return this.convertirFormula(this.buscarFormula(formulaId));
    }

    @Override
    public MiRespuestaRS guardarFormula(FormulaMedicaRq formulaRq) {
        // Paso 1. Campos obligatorios
        this.validarObjetoFormula(formulaRq);
        if (formulaRq.getMascotaId() == null) {
            throw new BadRequestException("Debe seleccionar una mascota");
        }
        if (formulaRq.getCitaId() == null) {
            throw new BadRequestException("Debe seleccionar la consulta (cita)");
        }
        if (formulaRq.getMedicoId() == null) {
            throw new BadRequestException("Debe seleccionar un veterinario");
        }

        // Paso 2. Regla 1 y 2: la mascota debe existir y tener un cliente registrado
        Mascota mascota = mascotaService.consultarMascota(formulaRq.getMascotaId());
        if (mascota.getCliente() == null) {
            throw new BadRequestException("La mascota no pertenece a ningún cliente registrado");
        }
        if (formulaRq.getClienteId() != null) {
            clienteService.consultarCliente(formulaRq.getClienteId());
            if (!mascota.getCliente().getClienteId().equals(formulaRq.getClienteId())) {
                throw new BadRequestException("La mascota no pertenece al cliente seleccionado");
            }
        }

        // Paso 3. Regla 3: el veterinario debe existir
        Optional<Medico> optMedico = medicoRepository.findById(formulaRq.getMedicoId());
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }

        // Paso 4. Regla 7: la cita debe ser de la misma mascota (y no estar cancelada)
        Cita cita = citaService.buscarCita(formulaRq.getCitaId());
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La consulta seleccionada pertenece a otra mascota");
        }
        if (cita.getEstado().equals("cancelada")) {
            throw new BadRequestException("No se puede formular sobre una cita cancelada");
        }

        // Paso 5. Regla 4: fecha de emision no anterior a la consulta
        this.validarFechaEmision(formulaRq, cita);
        Medicamento medicamento = this.buscarMedicamento(formulaRq.getMedicamentoId());

        // Evitar duplicados: mismo medicamento en la misma cita
        if (formulaMedicaRepository.existsByCitaIdAndMedicamentoIdAndActivoTrue(cita.getId(), medicamento.getId())) {
            throw new BadRequestException("Ya existe una fórmula activa con ese medicamento para esa consulta");
        }

        // Paso 6. Crear y guardar
        FormulaMedica formula = new FormulaMedica();
        formula.setCita(cita);
        formula.setMedico(optMedico.get());
        formula.setMedicamento(medicamento);
        formula.setFechaEmision(formulaRq.getFechaEmision());
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setFrecuencia(formulaRq.getFrecuencia().trim());
        formula.setDuracionDias(formulaRq.getDuracionDias());
        formula.setIndicaciones(formulaRq.getIndicaciones());
        formula.setActivo(true);
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);
        return this.respuestaExito("Fórmula médica guardada correctamente");
    }

    @Override
    public MiRespuestaRS actualizarFormula(FormulaMedicaRq formulaRq) {
        // Paso 1. Validar datos y buscar la formula
        this.validarObjetoFormula(formulaRq);
        FormulaMedica formula = this.buscarFormula(formulaRq.getFormulaId());

        // Paso 2. Una formula inactiva ya no se puede modificar
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new BadRequestException("La fórmula está inactiva y ya no se puede modificar");
        }

        // Paso 3. Solo se cambian los datos del tratamiento.
        // La mascota, la cita y el veterinario no se cambian porque son la parte clinica de la formula.
        this.validarFechaEmision(formulaRq, formula.getCita());
        formula.setMedicamento(this.buscarMedicamento(formulaRq.getMedicamentoId()));
        formula.setFechaEmision(formulaRq.getFechaEmision());
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setFrecuencia(formulaRq.getFrecuencia().trim());
        formula.setDuracionDias(formulaRq.getDuracionDias());
        formula.setIndicaciones(formulaRq.getIndicaciones());
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);
        return this.respuestaExito("Fórmula médica actualizada correctamente");
    }

    @Override
    public MiRespuestaRS inactivarFormula(Long formulaId) {
        // Regla 8: la formula esta ligada a informacion clinica, por eso no se borra, se inactiva
        FormulaMedica formula = this.buscarFormula(formulaId);
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new BadRequestException("La fórmula ya estaba inactiva");
        }
        formula.setActivo(false);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);
        return this.respuestaExito("Fórmula médica inactivada correctamente");
    }

    /**
     * Busca una fórmula, lanza error si no existe.
     *
     * @param formulaId id de la fórmula.
     * @return la fórmula.
     */
    private FormulaMedica buscarFormula(Long formulaId) {
        if (formulaId == null || formulaId <= 0) {
            throw new BadRequestException("El ID de la fórmula no puede ser nulo o menor que 1");
        }
        Optional<FormulaMedica> optFormula = formulaMedicaRepository.findById(formulaId);
        if (optFormula.isEmpty()) {
            throw new BadRequestException("Fórmula médica no encontrada");
        }
        return optFormula.get();
    }

    /**
     * Busca un medicamento, lanza error si no existe.
     *
     * @param medicamentoId id del medicamento.
     * @return el medicamento.
     */
    private Medicamento buscarMedicamento(Long medicamentoId) {
        Optional<Medicamento> optMedicamento = medicamentoRepository.findById(medicamentoId);
        if (optMedicamento.isEmpty()) {
            throw new BadRequestException("Medicamento no encontrado");
        }
        return optMedicamento.get();
    }

    /**
     * Regla 4: la fecha de emisión no puede ser anterior a la fecha de la consulta.
     *
     * @param formulaRq datos de la fórmula.
     * @param cita      cita asociada.
     */
    private void validarFechaEmision(FormulaMedicaRq formulaRq, Cita cita) {
        if (formulaRq.getFechaEmision().isBefore(cita.getFechaHora().toLocalDate())) {
            throw new BadRequestException("La fecha de emisión no puede ser anterior a la fecha de la consulta");
        }
    }

    /**
     * Regla 5 y 6: valida los campos obligatorios y que dosis y duración sean válidas.
     *
     * @param formulaRq datos de la fórmula.
     */
    private void validarObjetoFormula(FormulaMedicaRq formulaRq) {
        if (formulaRq == null) {
            throw new BadRequestException("Los datos de la fórmula son obligatorios");
        }
        if (formulaRq.getMedicamentoId() == null) {
            throw new BadRequestException("Debe seleccionar un medicamento");
        }
        if (formulaRq.getFechaEmision() == null) {
            throw new BadRequestException("La fecha de emisión es obligatoria");
        }
        if (formulaRq.getDosis() == null || formulaRq.getDosis().isBlank()) {
            throw new BadRequestException("La dosis es obligatoria");
        }
        if (formulaRq.getFrecuencia() == null || formulaRq.getFrecuencia().isBlank()) {
            throw new BadRequestException("La frecuencia es obligatoria");
        }
        if (formulaRq.getDuracionDias() == null || formulaRq.getDuracionDias() < 1
                || formulaRq.getDuracionDias() > MAX_DIAS_TRATAMIENTO) {
            throw new BadRequestException("La duración del tratamiento debe estar entre 1 y 365 días");
        }
    }

    /**
     * Convierte la entidad al objeto de respuesta.
     *
     * @param formula entidad.
     * @return objeto de respuesta.
     */
    private FormulaMedicaRs convertirFormula(FormulaMedica formula) {
        FormulaMedicaRs rs = new FormulaMedicaRs();
        rs.setId(formula.getId());
        rs.setCitaId(formula.getCita().getId());
        rs.setCitaFechaHora(formula.getCita().getFechaHora());
        rs.setMascotaId(formula.getCita().getMascota().getMascotaId());
        rs.setMascotaNombre(formula.getCita().getMascota().getNombreMascota());
        rs.setClienteId(formula.getCita().getMascota().getCliente().getClienteId());
        rs.setClienteNombre(formula.getCita().getMascota().getCliente().getNombres() + " "
                + formula.getCita().getMascota().getCliente().getApellidos());
        if (formula.getMedico() != null) {
            rs.setMedicoId(formula.getMedico().getId());
            rs.setMedicoNombre(formula.getMedico().getNombres() + " " + formula.getMedico().getApellidos());
        }
        rs.setMedicamentoId(formula.getMedicamento().getId());
        rs.setMedicamentoNombre(formula.getMedicamento().getNombre());
        rs.setDosis(formula.getDosis());
        rs.setFrecuencia(formula.getFrecuencia());
        rs.setDuracionDias(formula.getDuracionDias());
        rs.setIndicaciones(formula.getIndicaciones());
        rs.setFechaEmision(formula.getFechaEmision());
        rs.setActivo(formula.getActivo());
        rs.setFechaCreacionRegistro(formula.getFechaCreacionRegistro());
        rs.setFechaActualizacionRegistro(formula.getFechaActualizacionRegistro());
        return rs;
    }

    /**
     * Arma la respuesta de éxito.
     *
     * @param mensaje mensaje para el usuario.
     * @return respuesta.
     */
    private MiRespuestaRS respuestaExito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
