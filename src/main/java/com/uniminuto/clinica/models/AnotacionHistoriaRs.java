package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Clase que representa una anotacion de historia medica en la respuesta de los servicios.
 */
@Data
public class AnotacionHistoriaRs {

    /**
     * Identificador de la anotacion.
     */
    private Long id;

    /**
     * Identificador de la historia medica a la que pertenece.
     */
    private Long historiaId;

    /**
     * Identificador del medico que la escribio.
     */
    private Long medicoId;

    /**
     * Nombre completo del medico que la escribio.
     */
    private String medicoNombre;

    /**
     * Fecha y hora en que se creo la anotacion.
     */
    private LocalDateTime fecha;

    /**
     * Texto de la anotacion.
     */
    private String descripcion;
}
