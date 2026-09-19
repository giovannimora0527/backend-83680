package com.uniminuto.clinica.api;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uniminuto.clinica.entity.FormulaMedica;

/**
 * Define los servicios REST relacionados con las fórmulas médicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    /**
     * Lista las fórmulas médicas ordenadas desde la más reciente
     * hasta la más antigua.
     *
     * @return respuesta con la lista de fórmulas médicas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<FormulaMedica>> listarFormulaMedicas()
            throws BadRequestException;
}