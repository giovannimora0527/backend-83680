package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** Implementa las operaciones de fórmulas médicas. */
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    /** Repositorio de fórmulas médicas. */
    private FormulaMedicaRepository formulaMedicaRepository;

    /** Recibe el repositorio utilizado por el servicio. */
    public FormulaMedicaServiceImpl(FormulaMedicaRepository formulaMedicaRepository) {
        this.formulaMedicaRepository = formulaMedicaRepository;
    }

    @Override
    // Valida, evita duplicados y guarda una nueva fórmula médica.
    public MiRespuestaRS crearFormulaMedica(FormulaMedicaRq formulaRq) {
        if (formulaRq == null || formulaRq.getCitaId() == null || formulaRq.getCitaId() <= 0
                || formulaRq.getMedicamentoId() == null || formulaRq.getMedicamentoId() <= 0
                || formulaRq.getDosis() == null || formulaRq.getDosis().isBlank()) {
            throw new BadRequestException("La cita, el medicamento y la dosis son obligatorios");
        }

        if (formulaMedicaRepository.existsByCitaIdAndMedicamentoId(
                formulaRq.getCitaId(), formulaRq.getMedicamentoId())) {
            throw new BadRequestException("El medicamento ya está formulado para esta cita");
        }

        FormulaMedica formula = new FormulaMedica();
        formula.setCitaId(formulaRq.getCitaId());
        formula.setMedicamentoId(formulaRq.getMedicamentoId());
        formula.setDosis(formulaRq.getDosis().trim());
        formula.setIndicaciones(
                formulaRq.getIndicaciones() == null ? null : formulaRq.getIndicaciones().trim());
        formula.setFechaCreacionRegistro(java.time.LocalDateTime.now());
        formulaMedicaRepository.save(formula);

        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Fórmula médica creada correctamente");
        return respuesta;
    }

    @Override
    // Consulta todas las fórmulas y las ordena por fecha de creación.
    public List<FormulaMedica> listarFormulasMedicas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }
}
