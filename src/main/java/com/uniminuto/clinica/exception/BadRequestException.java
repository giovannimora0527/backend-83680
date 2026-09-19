package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Clase de excepción personalizada para manejar errores de solicitud incorrecta (Bad Request).
 */
public class BadRequestException extends RuntimeException {

    /** Estado HTTP asociado al error. */
    private final HttpStatus status;

    /** Crea una excepción de solicitud inválida con el mensaje indicado. */
    public BadRequestException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    /** Devuelve el estado HTTP que debe informarse al cliente. */
    public HttpStatus getStatus() {
        return status;
    }
}
