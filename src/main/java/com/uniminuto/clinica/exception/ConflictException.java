package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Conflicto con el estado actual de los datos, por ejemplo una cita
 * duplicada o un cruce de horario (HTTP 409).
 */
public class ConflictException extends ApiException {

    /**
     * Crea la excepción de conflicto.
     *
     * @param message mensaje para el usuario.
     */
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}