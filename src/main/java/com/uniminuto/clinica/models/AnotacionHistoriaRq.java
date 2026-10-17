package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos de entrada para agregar una anotación a una historia clínica.
 */
@Data
public class AnotacionHistoriaRq {

    /** Id de la historia clínica a la que se agrega la anotación. */
    private Long historiaId;

    /** Id del veterinario que escribe la anotación. */
    private Long medicoId;

    /** Descripción de lo ocurrido. */
    private String descripcion;

    /** Observación adicional (opcional). */
    private String observacion;

    /** Diagnóstico o evolución del paciente (opcional). */
    private String diagnostico;
}