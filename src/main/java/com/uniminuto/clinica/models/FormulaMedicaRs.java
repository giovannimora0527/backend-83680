package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Clase que representa una formula medica en la respuesta de los servicios.
 */
@Data
public class FormulaMedicaRs {

    /**
     * Identificador de la formula medica.
     */
    private Long id;

    /**
     * Identificador de la cita a la que pertenece la formula.
     */
    private Long citaId;

    /**
     * Identificador del medicamento recetado.
     */
    private Long medicamentoId;

    /**
     * Nombre del medicamento recetado.
     */
    private String medicamentoNombre;

    /**
     * Dosis indicada.
     */
    private String dosis;

    /**
     * Indicaciones adicionales.
     */
    private String indicaciones;

    /**
     * Fecha y hora en que se creo la formula.
     */
    private LocalDateTime fechaCreacionRegistro;

    /**
     * Fecha y hora de la ultima actualizacion de la formula.
     */
    private LocalDateTime fechaActualizacionRegistro;
}
