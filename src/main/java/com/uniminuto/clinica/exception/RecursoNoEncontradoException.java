package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción utilizada cuando no existe un recurso solicitado.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoException extends RuntimeException {

    /**
     * Construye una excepción de recurso no encontrado.
     *
     * @param message mensaje descriptivo del error.
     */
    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
