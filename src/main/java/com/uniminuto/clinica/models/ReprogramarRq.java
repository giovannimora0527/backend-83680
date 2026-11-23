package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Datos para cambiar la fecha y hora de una cita desde el calendario.
 */
@Data
public class ReprogramarRq {
    /** Id de la cita. */
    private Long citaId;
    /** Nuevo inicio. */
    private LocalDateTime fechaHora;
    /** Nuevo fin. */
    private LocalDateTime fechaHoraFin;
}
