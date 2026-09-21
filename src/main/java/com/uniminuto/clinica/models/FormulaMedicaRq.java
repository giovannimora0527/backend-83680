package com.uniminuto.clinica.models;

import lombok.Data;

@Data
/** Datos recibidos para crear una fórmula médica. */
public class FormulaMedicaRq {
    /** Identificador de la cita relacionada. */
    private Integer citaId;
    /** Identificador del medicamento. */
    private Integer medicamentoId;
    /** Dosis indicada. */
    private String dosis;
    /** Indicaciones para el paciente. */
    private String indicaciones;
}
