package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Se incumple una regla de negocio, por ejemplo modificar una cita
 * cancelada (HTTP 422).
 */
public class BusinessRuleException extends ApiException {

    /**
     * Crea la excepción de regla de negocio.
     *
     * @param message mensaje para el usuario.
     */
    public BusinessRuleException(String message) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, message);
    }
}