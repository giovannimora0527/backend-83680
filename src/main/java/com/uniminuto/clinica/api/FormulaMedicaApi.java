package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Interfaz que define el contrato de la API para Fórmulas Médicas.
 *
 * @author Alma Hernandez
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    /**
     * Endpoint para listar todas las fórmulas médicas del inventario
     * por fecha de creación de la más reciente a la más antigua.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaMedica>> listarFormulasMedicas() throws BadRequestException;
}