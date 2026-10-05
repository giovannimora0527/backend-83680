package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormulaMedicaServiceImpl
        implements FormulaMedicaService {

    private final FormulaMedicaRepository
            formulaMedicaRepository;

    public FormulaMedicaServiceImpl(
            FormulaMedicaRepository formulaMedicaRepository
    ) {
        this.formulaMedicaRepository =
                formulaMedicaRepository;
    }

    @Override
    public List<FormulaMedica> listarFormulas()
            throws BadRequestException {

        return formulaMedicaRepository
                .findAllByOrderByFechaCreacionRegistroDesc();
    }

    @Override
    public FormulaMedica crearFormula(
            FormulaMedica formula
    ) throws BadRequestException {

        validarFormula(formula);

        formula.setDosis(
                formula.getDosis().trim()
        );

        formula.setIndicaciones(
                formula.getIndicaciones().trim()
        );

        formula.setFechaCreacionRegistro(
                LocalDateTime.now()
        );

        formula.setFechaActualizacionRegistro(
                null
        );

        return formulaMedicaRepository.save(
                formula
        );
    }

    @Override
    public FormulaMedica actualizarFormula(
            Long id,
            FormulaMedica formula
    ) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException(
                    "El ID de la fórmula médica es obligatorio"
            );
        }

        FormulaMedica existente =
                formulaMedicaRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "La fórmula médica no existe"
                                )
                        );

        validarFormula(formula);

        existente.setCitaId(
                formula.getCitaId()
        );

        existente.setMedicamentoId(
                formula.getMedicamentoId()
        );

        existente.setDosis(
                formula.getDosis().trim()
        );

        existente.setIndicaciones(
                formula.getIndicaciones().trim()
        );

        existente.setFechaActualizacionRegistro(
                LocalDateTime.now()
        );

        return formulaMedicaRepository.save(
                existente
        );
    }

    private void validarFormula(
            FormulaMedica formula
    ) throws BadRequestException {

        if (formula == null) {
            throw new BadRequestException(
                    "La fórmula médica es obligatoria"
            );
        }

        if (formula.getCitaId() == null) {
            throw new BadRequestException(
                    "La cita es obligatoria"
            );
        }

        if (formula.getMedicamentoId() == null) {
            throw new BadRequestException(
                    "El medicamento es obligatorio"
            );
        }

        if (formula.getDosis() == null
                || formula.getDosis().trim().isEmpty()) {

            throw new BadRequestException(
                    "La dosis es obligatoria"
            );
        }

        if (formula.getIndicaciones() == null
                || formula.getIndicaciones()
                        .trim()
                        .isEmpty()) {

            throw new BadRequestException(
                    "Las indicaciones son obligatorias"
            );
        }
    }
}