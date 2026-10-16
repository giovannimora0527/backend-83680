package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Clase de excepción personalizada para cuando un registro no existe (Not Found).
 */
public class NotFoundException extends RuntimeException {

    /** Estado HTTP asociado al error. */
    private final HttpStatus status;

    /** Crea una excepción de registro no encontrado con el mensaje indicado. */
    public NotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }

    /** Devuelve el estado HTTP que debe informarse al cliente. */
    public HttpStatus getStatus() {
        return status;
    }
}
