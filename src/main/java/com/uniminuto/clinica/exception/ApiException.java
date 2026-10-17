package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base de la API. Cada subclase define el código HTTP
 * con el que se responderá al cliente.
 */
public abstract class ApiException extends RuntimeException {

    /** Código HTTP asociado al error. */
    private final HttpStatus status;

    /**
     * Crea una excepción controlada de la API.
     *
     * @param status  código HTTP de la respuesta.
     * @param message mensaje comprensible para el usuario final.
     */
    protected ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * Obtiene el código HTTP asociado al error.
     *
     * @return código HTTP del error.
     */
    public HttpStatus getStatus() {
        return status;
    }
}
