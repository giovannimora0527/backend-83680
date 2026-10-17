package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de entrada para crear o actualizar una cita.
 */
@Data
public class CitaRq {

    /** Id de la cita (solo se usa al actualizar). */
    private Long id;

    /** Id del cliente dueno de la mascota. */
    private Long clienteId;

    /** Id de la mascota que sera atendida. */
    private Integer mascotaId;

    /** Id del veterinario asignado. */
    private Long medicoId;

    /** Fecha y hora de la cita. */
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    private String motivo;

    /** Estado de la cita (PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA, PROGRAMADA). */
    private String estado;
}
