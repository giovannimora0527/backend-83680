package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
/** Datos recibidos para gestionar una historia médica. */
public class HistoriaMedicaRq {
    /** Identificador de la historia cuando se actualiza. */
    private Long historiaMedicaId;
    /** Identificador del cliente. */
    private Long clienteId;
    /** Identificador de la mascota (paciente). */
    private Integer mascotaId;
    /** Identificador de la cita atendida. */
    private Long citaId;
    /** Identificador del veterinario. */
    private Long medicoId;
    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;
    /** Motivo de la consulta. */
    private String motivo;
    /** Diagnóstico de la consulta. */
    private String diagnostico;
    /** Observaciones de la consulta. */
    private String observaciones;
    /** Estado de la historia: ABIERTA o CERRADA. */
    private String estado;
}
