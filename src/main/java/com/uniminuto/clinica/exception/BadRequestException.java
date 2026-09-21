package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Clase de excepción personalizada para manejar errores de solicitud incorrecta (Bad Request).
 */
public class BadRequestException extends RuntimeException {

    /**
     * Estado HTTP que se devuelve al cliente cuando se lanza esta excepción.
     */
    private final HttpStatus status;

    /**
     * Crea la excepción con el mensaje que se le mostrará al cliente.
     *
     * @param message descripción del error de la solicitud.
     */
    public BadRequestException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    /**
     * Obtiene el estado HTTP asociado a la excepción.
     *
     * @return el estado HTTP (siempre 400 Bad Request).
     */
    public HttpStatus getStatus() {
        return status;
    }
}
