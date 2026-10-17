package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;

/**
 * Recurso no encontrado, por ejemplo un cliente, mascota o cita inexistente (HTTP 404).
 */
public class NotFoundException extends ApiException {

    /**
     * Crea la excepcion de recurso no encontrado.
     *
     * @param message mensaje para el usuario.
     */
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}