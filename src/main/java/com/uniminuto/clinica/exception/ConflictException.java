package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Clase de excepción personalizada para cuando la operación choca con el estado
 * actual de la información (Conflict), por ejemplo un registro duplicado,
 * un horario ocupado o un registro que ya no se puede modificar.
 */
public class ConflictException extends RuntimeException {

    /** Estado HTTP asociado al error. */
    private final HttpStatus status;

    /** Crea una excepción de conflicto con el mensaje indicado. */
    public ConflictException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

    /** Devuelve el estado HTTP que debe informarse al cliente. */
    public HttpStatus getStatus() {
        return status;
    }
}
