package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;
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
    /** Fecha y hora de la cita asociada. */
    private LocalDateTime citaFechaHora;
    /** Id de la mascota. */
    private Integer mascotaId;
    /** Nombre de la mascota. */
    private String mascotaNombre;
    /** Id del cliente. */
    private Long clienteId;
    /** Nombre del cliente. */
    private String clienteNombre;
    /** Id del veterinario. */
    private Long medicoId;
    /** Nombre del veterinario. */
    private String medicoNombre;
    /** Frecuencia. */
    private String frecuencia;
    /** Duracion del tratamiento en dias. */
    private Integer duracionDias;
    /** Fecha de emision. */
    private LocalDate fechaEmision;
    /** Si la formula esta vigente. */
    private Boolean activo;
}
