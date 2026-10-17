package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de entrada para registrar una historia clínica.
 */
@Data
public class HistoriaClinicaRq {

    /** Id del cliente dueño de la mascota. */
    private Long clienteId;

    /** Id de la mascota atendida. */
    private Integer mascotaId;

    /** Id de la cita atendida que origina la historia. */
    private Long citaId;

    /** Id del veterinario que realizó la consulta. */
    private Long medicoId;

    /** Fecha y hora de la consulta (si no viene, se usa la de la cita). */
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    private String motivo;

    /** Diagnóstico de la consulta. */
    private String diagnostico;

    /** Observaciones generales (opcional). */
    private String observaciones;
}