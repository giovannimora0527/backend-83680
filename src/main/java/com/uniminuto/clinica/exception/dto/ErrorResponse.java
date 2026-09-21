package com.uniminuto.clinica.exception.dto;

import lombok.Data;

/**
 * Clase que representa la estructura de la respuesta de error
 * para las excepciones manejadas por el GlobalExceptionHandler.
 */
@Data
public class ErrorResponse {

    /**
     * Código de estado HTTP del error.
     */
    private int status;

    /**
     * Nombre corto del tipo de error.
     */
    private String error;

    /**
     * Mensaje descriptivo del error.
     */
    private String message;

    /**
     * Crea una respuesta de error.
     *
     * @param status  código de estado HTTP.
     * @param error   nombre corto del tipo de error.
     * @param message mensaje descriptivo del error.
     */
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
