package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
/** Datos recibidos para registrar un bloqueo de horario de un veterinario. */
public class BloqueoHorarioRq {
    /** Identificador del veterinario. */
    private Long medicoId;
    /** Fecha y hora de inicio del bloqueo. */
    private LocalDateTime fechaHoraInicio;
    /** Fecha y hora de fin del bloqueo. */
    private LocalDateTime fechaHoraFin;
    /** Motivo del bloqueo de agenda. */
    private String motivo;
}
