package com.uniminuto.clinica.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Modelo de respuesta para una anotación de historia médica.
 */
@Data
public class AnotacionHistoriaRs {

    /**
     * Identificador de la anotación.
     */
    private Long id;

    /**
     * Identificador de la historia médica asociada.
     */
    private Integer historiaId;

    /**
     * Identificador del médico que realizó la anotación.
     */
    private Integer medicoId;

    /**
     * Fecha en la que se realizó la anotación.
     */
    private LocalDateTime fecha;

    /**
     * Descripción de la anotación.
     */
    private String descripcion;
}