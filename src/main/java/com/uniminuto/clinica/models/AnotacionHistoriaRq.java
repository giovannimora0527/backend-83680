package com.uniminuto.clinica.models;

import lombok.Data;

@Data
/** Datos recibidos para crear o actualizar una anotación. */
public class AnotacionHistoriaRq {
    /** Identificador de la anotación cuando se actualiza. */
    private Long anotacionHistoriaId;
    /** Identificador de la historia médica. */
    private Long historiaMedicaId;
    /** Identificador del médico que registra la anotación. */
    private Long medicoId;
    /** Descripción de la anotación. */
    private String descripcion;
}
