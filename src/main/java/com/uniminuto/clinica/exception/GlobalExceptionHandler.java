package com.uniminuto.clinica.exception;

import com.uniminuto.clinica.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Traduce las excepciones a respuestas JSON consistentes con la forma
 * {status, error, message}. Nunca expone detalles técnicos al cliente.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
        /**
     * Maneja violaciones de integridad de la base de datos (duplicados, llaves foráneas).
     *
     * @param ex excepción de integridad.
     * @return respuesta 409 con un mensaje claro.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violacion de integridad de datos", ex);
        return construir(HttpStatus.CONFLICT,
                "No se pudo guardar: el registro entra en conflicto con datos existentes.");
    }

    /**
     * Maneja los errores controlados de la aplicación (400, 404, 409 y 422).
     *
     * @param ex excepción controlada.
     * @return respuesta con el código HTTP y el mensaje del error.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return construir(ex.getStatus(), ex.getMessage());
    }

    /**
     * Maneja JSON mal formado o fechas con formato inválido.
     *
     * @param ex excepción de lectura del cuerpo.
     * @return respuesta 400 con un mensaje genérico.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "Los datos enviados no tienen un formato válido.");
    }

    /**
     * Maneja parámetros de URL con un tipo incorrecto (por ejemplo un id no numérico).
     *
     * @param ex excepción de tipo de argumento.
     * @return respuesta 400 indicando el parámetro inválido.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "El valor del parámetro '" + ex.getName() + "' no es válido.");
    }

    /**
     * Maneja la ausencia de un parámetro obligatorio en la URL.
     *
     * @param ex excepción de parámetro faltante.
     * @return respuesta 400 indicando el parámetro que falta.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleParametroFaltante(MissingServletRequestParameterException ex) {
        return construir(HttpStatus.BAD_REQUEST,
                "Falta el parámetro obligatorio '" + ex.getParameterName() + "'.");
    }

    /**
     * Maneja rutas inexistentes.
     *
     * @param ex excepción de recurso no encontrado.
     * @return respuesta 404 con un mensaje genérico.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaInexistente(NoResourceFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, "El recurso solicitado no existe.");
    }

    /**
     * Maneja cualquier error inesperado: se registra en el log y se responde
     * con un mensaje genérico para no exponer información técnica.
     *
     * @param ex excepción inesperada.
     * @return respuesta 500 con un mensaje genérico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Error inesperado", ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intente nuevamente más tarde.");
    }

    /**
     * Construye la respuesta de error con la estructura estándar.
     *
     * @param status  código HTTP.
     * @param mensaje mensaje para el usuario.
     * @return respuesta de error.
     */
    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), mensaje));
    }
}