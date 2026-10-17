package com.uniminuto.clinica.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Modelo de respuesta para una cita.
 */
@Data
public class CitaRs {

    /**
     * Identificador de la cita.
     */
    private Long id;

    /**
     * Identificador del cliente asociado a la cita.
     */
    private Integer clienteId;

    /**
     * Identificador de la mascota asociada a la cita.
     */
    private Integer mascotaId;

    /**
     * Identificador del médico asociado a la cita.
     */
    private Integer medicoId;

    /**
     * Fecha y hora programada para la cita.
     */
    private LocalDateTime fechaHora;

    /**
     * Estado actual de la cita.
     */
    private String estado;

    /**
     * Motivo de la cita.
     */
    private String motivo;
}