package com.uniminuto.clinica.exception.dto;

import lombok.Data;

/**
 * Clase que representa la estructura de la respuesta de error
 * para las excepciones manejadas por el GlobalExceptionHandler.
 */
@Data
public class ErrorResponse {

    /** Código numérico del error. */
    private int status;
    /** Nombre o tipo del error. */
    private String error;
    /** Mensaje explicado al consumidor de la API. */
    private String message;

    /** Construye una respuesta de error con sus datos principales. */
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
