package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

/**
 * Datos que llegan del front para crear o editar una formula medica.
 */
@Data
public class FormulaMedicaRq {
    /** Id de la formula (solo para editar). */
    private Long formulaId;
    /** Id del cliente. */
    private Long clienteId;
    /** Id de la mascota. */
    private Integer mascotaId;
    /** Id de la cita (consulta). */
    private Long citaId;
    /** Id del veterinario. */
    private Long medicoId;
    /** Id del medicamento. */
    private Long medicamentoId;
    /** Fecha de emision. */
    private LocalDate fechaEmision;
    /** Dosis. */
    private String dosis;
    /** Frecuencia. */
    private String frecuencia;
    /** Duracion del tratamiento en dias. */
    private Integer duracionDias;
    /** Indicaciones. */
    private String indicaciones;
}
