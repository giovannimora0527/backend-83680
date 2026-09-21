package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Clase que representa la solicitud para crear o actualizar una cita.
 */
@Data
public class CitaRq {

    /**
     * Identificador de la cita. Es obligatorio al actualizar y se ignora al crear.
     */
    private Long citaId;

    /**
     * Identificador del cliente que solicita la cita.
     */
    private Long clienteId;

    /**
     * Identificador de la mascota que será atendida (debe pertenecer al cliente).
     */
    private Integer mascotaId;

    /**
     * Identificador del médico que atiende la cita.
     */
    private Long medicoId;

    /**
     * Fecha y hora de la cita, en formato ISO (por ejemplo 2026-10-01T10:30:00).
     */
    private LocalDateTime fechaHora;

    /**
     * Estado de la cita. Es obligatorio al actualizar; al crear siempre queda como "programada".
     */
    private String estado;

    /**
     * Motivo de la consulta.
     */
    private String motivo;
}
