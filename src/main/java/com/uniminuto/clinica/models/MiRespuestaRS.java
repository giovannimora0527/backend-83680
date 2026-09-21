package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Respuesta generica de los servicios que guardan o actualizan informacion.
 */
@Data
public class MiRespuestaRS {

    /**
     * Codigo de estado de la operacion (200 cuando salio bien).
     */
    private int status;

    /**
     * Mensaje con el resultado de la operacion.
     */
    private String message;
}
