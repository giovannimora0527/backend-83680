package com.uniminuto.clinica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de errores de la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja recursos que no existen.
     *
     * @param ex excepción generada.
     * @return respuesta estructurada con el error.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> recursoNoEncontrado(RecursoNoEncontradoException ex) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", 404);
        respuesta.put("error", "Recurso no encontrado");
        respuesta.put("mensaje", ex.getMessage());
        return respuesta;
    }

    /**
     * Maneja argumentos inválidos, como rangos de fechas incorrectos.
     *
     * @param ex excepción generada.
     * @return respuesta estructurada con el error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> argumentoInvalido(IllegalArgumentException ex) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", 400);
        respuesta.put("error", "Solicitud inválida");
        respuesta.put("mensaje", ex.getMessage());
        return respuesta;
    }

    /**
     * Maneja errores de validación de los DTO.
     *
     * @param ex excepción de validación.
     * @return detalle de la validación.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validacion(MethodArgumentNotValidException ex) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("status", 400);
        respuesta.put("error", "Error de validación");
        respuesta.put(
                "detalles",
                ex.getBindingResult().getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList()
        );
        return respuesta;
    }
}
