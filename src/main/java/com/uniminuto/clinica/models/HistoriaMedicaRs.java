package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos de una historia clinica que se envian al front.
 */
@Data
public class HistoriaMedicaRs {
    /** Id de la historia. */
    private Long id;
    /** Id del cliente. */
    private Long clienteId;
    /** Nombre completo del cliente. */
    private String clienteNombre;
    /** Id de la mascota. */
    private Integer mascotaId;
    /** Nombre de la mascota. */
    private String mascotaNombre;
    /** Id de la cita. */
    private Long citaId;
    /** Id del veterinario. */
    private Long medicoId;
    /** Nombre completo del veterinario. */
    private String medicoNombre;
    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;
    /** Motivo de la consulta. */
    private String motivo;
    /** Diagnostico. */
    private String diagnostico;
    /** Observaciones. */
    private String observaciones;
    /** Estado de la cita asociada. */
    private String estado;
}
