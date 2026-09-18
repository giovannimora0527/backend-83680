package com.uniminuto.clinica.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Modelo de solicitud para crear una historia médica.
 */
@Data
public class HistoriaMedicaRq {

    /**
     * Identificador del paciente asociado a la historia médica.
     */
    @NotNull(message = "El identificador del paciente es obligatorio.")
    private Integer pacienteId;
}