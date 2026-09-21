package com.uniminuto.clinica.models;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Clase que representa la solicitud para crear o actualizar 
 * una anotación en la historia médica.
 *
 * @author Alma Hernandez
 */
@Data
public class AnotacionHistoriaRq {

    /**
     * Identificador único de la anotación.
     * Requerido solo para operaciones de actualización.
     */
    private Long id;

    /**
     * Identificador de la historia médica a la que pertenece.
     */
    private Integer historiaId;

    /**
     * Identificador del médico que realiza la anotación.
     */
    private Integer medicoId;

    /**
     * Fecha y hora en la que se realiza la anotación.
     */
    private LocalDateTime fecha;

    /**
     * Descripción detallada de la anotación médica.
     */
    private String descripcion;
}