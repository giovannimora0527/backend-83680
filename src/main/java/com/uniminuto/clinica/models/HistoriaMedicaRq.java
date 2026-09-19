package com.uniminuto.clinica.models;

import lombok.Data;

@Data
/** Datos recibidos para gestionar una historia médica. */
public class HistoriaMedicaRq {
    /** Identificador de la historia cuando se actualiza. */
    private Long historiaMedicaId;
    /** Identificador del paciente. */
    private Integer pacienteId;
}
