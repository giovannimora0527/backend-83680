package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface FormulaMedicaService {

    List<FormulaMedica> listarFormulas()
            throws BadRequestException;

    FormulaMedica crearFormula(
            FormulaMedica formula
    ) throws BadRequestException;

    FormulaMedica actualizarFormula(
            Long id,
            FormulaMedica formula
    ) throws BadRequestException;
}