package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Clase que representa la solicitud para crear o actualizar una anotación de historia médica.
 */
@Data
public class AnotacionHistoriaRq {

    /**
     * Identificador de la anotación. Es obligatorio al actualizar y se ignora al crear.
     */
    private Long anotacionId;

    /**
     * Identificador de la historia médica. Es obligatorio al crear y se ignora al actualizar.
     */
    private Long historiaId;

    /**
     * Identificador del médico que escribe la anotación.
     */
    private Long medicoId;

    /**
     * Texto de la anotación.
     */
    private String descripcion;
}
