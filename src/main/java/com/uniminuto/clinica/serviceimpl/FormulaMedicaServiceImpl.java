package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
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
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/** Implementa las operaciones de fórmulas médicas. */
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    /** Repositorio de fórmulas médicas. */
    private FormulaMedicaRepository formulaMedicaRepository;

    @Autowired
    /** Repositorio de mascotas. */
    private MascotaRepository mascotaRepository;

    @Autowired
    /** Repositorio de citas. */
    private CitaRepository citaRepository;

    @Autowired
    /** Repositorio de médicos. */
    private MedicoRepository medicoRepository;

    @Autowired
    /** Repositorio de medicamentos. */
    private MedicamentoRepository medicamentoRepository;

    @Override
    // Valida las reglas de negocio, evita duplicados y guarda una nueva fórmula médica.
    public MiRespuestaRS crearFormulaMedica(FormulaMedicaRq formulaRq) {
        validarFormula(formulaRq);
        if (formulaRq.getMascotaId() == null || formulaRq.getCitaId() == null) {
            throw new BadRequestException("La mascota y la consulta (cita) son obligatorias");
        }

        // Regla 1 y 2: la mascota debe existir y pertenecer a un cliente registrado y activo.
        Mascota mascota = mascotaRepository.findById(formulaRq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("Mascota no encontrada"));
        if (mascota.getCliente() == null || !Boolean.TRUE.equals(mascota.getCliente().getActivo())) {
            throw new BadRequestException("La mascota no pertenece a un cliente registrado");
        }

        // Regla 7: la consulta debe ser de la misma mascota.
        Cita cita = citaRepository.findById(formulaRq.getCitaId())
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La consulta seleccionada pertenece a otra mascota");
        }
        if (Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new BadRequestException("No se puede generar una fórmula para una cita cancelada");
        }

        if (formulaMedicaRepository.existsByCita_IdAndMedicamento_Id(
                formulaRq.getCitaId(), formulaRq.getMedicamentoId())) {
            throw new ConflictException("El medicamento ya está formulado para esta cita");
        }

        FormulaMedica formula = new FormulaMedica();
        formula.setMascota(mascota);
        formula.setCita(cita);
        asignarDatos(formula, formulaRq);
        formula.setActivo(true);
        formula.setFechaCreacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.CREATED.value(), "Fórmula médica creada correctamente");
    }

    @Override
    // Consulta todas las fórmulas y las ordena por fecha de creación.
    public List<FormulaMedica> listarFormulasMedicas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    // Valida el identificador y devuelve la fórmula si existe.
    public FormulaMedica consultarFormulaMedica(Long formulaId) {
        if (formulaId == null) {
            throw new BadRequestException("El ID de la fórmula no puede ser nulo");
        }
        return buscarFormula(formulaId);
    }

    @Override
    // Actualiza los datos de la fórmula; la mascota y la consulta no se pueden cambiar.
    public MiRespuestaRS actualizarFormulaMedica(FormulaMedicaRq formulaRq) {
        validarFormula(formulaRq);
        if (formulaRq.getFormulaId() == null) {
            throw new BadRequestException("El ID de la fórmula es obligatorio para actualizar");
        }

        FormulaMedica formula = buscarFormula(formulaRq.getFormulaId());
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new ConflictException("La fórmula está inactiva y ya no se puede modificar");
        }
        if (formulaMedicaRepository.existsByCita_IdAndMedicamento_IdAndIdNot(
                formula.getCita().getId(), formulaRq.getMedicamentoId(), formula.getId())) {
            throw new ConflictException("El medicamento ya está formulado para esta cita");
        }

        asignarDatos(formula, formulaRq);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.OK.value(), "Fórmula médica actualizada correctamente");
    }

    @Override
    // Regla 8: la fórmula hace parte de la información clínica, por eso se inactiva y no se borra.
    public MiRespuestaRS inactivarFormulaMedica(Long formulaId) {
        if (formulaId == null) {
            throw new BadRequestException("El ID de la fórmula es obligatorio para inactivar");
        }

        FormulaMedica formula = buscarFormula(formulaId);
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new ConflictException("La fórmula ya se encuentra inactiva");
        }

        formula.setActivo(false);
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        return respuesta(HttpStatus.OK.value(), "Fórmula médica inactivada correctamente");
    }

    private void asignarDatos(FormulaMedica formula, FormulaMedicaRq formulaRq) {
        // Regla 3: el veterinario debe existir. También se valida el medicamento.
        Medico medico = medicoRepository.findById(formulaRq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("Veterinario no encontrado"));
        Medicamento medicamento = medicamentoRepository.findById(formulaRq.getMedicamentoId())
                .orElseThrow(() -> new NotFoundException("Medicamento no encontrado"));

        // Regla 4: la fecha de emisión no puede ser anterior a la fecha de la consulta.
        if (formulaRq.getFechaEmision().isBefore(formula.getCita().getFechaHora().toLocalDate())) {
            throw new BadRequestException("La fecha de emisión no puede ser anterior a la fecha de la consulta");
        }

        formula.setMedico(medico);
        formula.setMedicamento(medicamento);
        formula.setFechaEmision(formulaRq.getFechaEmision());
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setFrecuencia(formulaRq.getFrecuencia().trim());
        formula.setDuracionDias(formulaRq.getDuracionDias());
        formula.setIndicaciones(formulaRq.getIndicaciones().trim());
    }

    private FormulaMedica buscarFormula(Long formulaId) {
        // Busca la fórmula o informa que no existe.
        return formulaMedicaRepository.findById(formulaId)
                .orElseThrow(() -> new NotFoundException("Fórmula médica no encontrada"));
    }

    private void validarFormula(FormulaMedicaRq formulaRq) {
        // Regla 6: campos obligatorios.
        if (formulaRq == null || formulaRq.getMedicoId() == null || formulaRq.getMedicamentoId() == null
                || formulaRq.getFechaEmision() == null
                || formulaRq.getDosis() == null || formulaRq.getDosis().isBlank()
                || formulaRq.getFrecuencia() == null || formulaRq.getFrecuencia().isBlank()
                || formulaRq.getDuracionDias() == null
                || formulaRq.getIndicaciones() == null || formulaRq.getIndicaciones().isBlank()) {
            throw new BadRequestException(
                    "Veterinario, medicamento, fecha de emisión, dosis, frecuencia, duración e indicaciones son obligatorios");
        }

        // Regla 5: la dosis debe indicar una cantidad y la duración debe ser un número de días válido.
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
        // Crea una respuesta estándar para informar el resultado de la operación.
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
