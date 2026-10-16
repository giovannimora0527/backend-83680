package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos que llegan del front para crear una historia clinica.
 */
@Data
public class HistoriaMedicaRq {
    /** Id de la historia (solo para editar). */
    private Long historiaId;
    /** Id del cliente dueño de la mascota. */
    private Long clienteId;
    /** Id de la mascota. */
    private Integer mascotaId;
    /** Id de la cita atendida. */
    private Long citaId;
    /** Id del veterinario. */
    private Long medicoId;
    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;
    /** Motivo de la consulta. */
    private String motivo;
    /** Diagnostico. */
    private String diagnostico;
    /** Observaciones. */
    private String observaciones;
}
