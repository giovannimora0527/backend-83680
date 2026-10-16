package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

@Data
/** Datos recibidos para crear o actualizar una fórmula médica. */
public class FormulaMedicaRq {
    /** Identificador de la fórmula cuando se actualiza. */
    private Long formulaId;
    /** Identificador de la mascota. */
    private Integer mascotaId;
    /** Identificador de la cita (consulta) relacionada. */
    private Long citaId;
    /** Identificador del veterinario que formula. */
    private Long medicoId;
    /** Identificador del medicamento. */
    private Long medicamentoId;
    /** Fecha de emisión de la fórmula. */
    private LocalDate fechaEmision;
    /** Dosis indicada. */
    private String dosis;
    /** Frecuencia con la que se da el medicamento. */
    private String frecuencia;
    /** Duración del tratamiento en días. */
    private Integer duracionDias;
    /** Indicaciones para el paciente. */
    private String indicaciones;
}
