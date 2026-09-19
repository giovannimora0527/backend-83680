package com.uniminuto.clinica.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.uniminuto.clinica.entity.FormulaMedica;

/**
 * Define las operaciones relacionadas con las fórmulas médicas.
 */
public interface FormulaMedicaService {

    /**
     * Obtiene las fórmulas médicas ordenadas desde la más reciente
     * hasta la más antigua según la fecha de creación.
     *
     * @return lista de fórmulas médicas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    List<FormulaMedica> obtenerFormulaMedicas() throws BadRequestException;

}