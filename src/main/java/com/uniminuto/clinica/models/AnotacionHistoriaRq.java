package com.uniminuto.clinica.models;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Datos utilizados para crear o actualizar una anotación de historia.
 */
@Data
public class AnotacionHistoriaRq {

    /**
     * Identificador de la historia médica asociada.
     */
    private Integer historiaId;

    /**
     * Identificador del médico que realiza la anotación.
     */
    private Integer medicoId;

    /**
     * Fecha de la anotación.
     */
    private LocalDateTime fecha;

    /**
     * Descripción de la anotación médica.
     */
    private String descripcion;
}