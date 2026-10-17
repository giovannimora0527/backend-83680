package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Datos de entrada inválidos o incompletos (HTTP 400).
 */
public class BadRequestException extends ApiException {

    /**
     * Crea la excepción de solicitud incorrecta.
     *
     * @param message mensaje para el usuario.
     */
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}