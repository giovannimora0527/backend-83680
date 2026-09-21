package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que implementa la API de Fórmulas Médicas.
 * Expone los endpoints para la gestión de fórmulas médicas.
 *
 * @author Alma Hernandez
 */

@RestController

public class FormulaMedicaApiController implements FormulaMedicaApi {
    /**
     * Servicio inyectado para la lógica de negocio de fórmulas médicas.
     */
    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * Obtiene la lista de todas las fórmulas médicas registradas en el sistema.
     */
    @Override
    public ResponseEntity<List<FormulaMedica>> listarFormulasMedicas() throws BadRequestException {
        try {
            // Delega al servicio y retorna la entidad directamente
            List<FormulaMedica> formulas = formulaMedicaService.listarFormulasMedicas();
            return ResponseEntity.ok(formulas);
        } catch (Exception e) {
            throw new BadRequestException("Error al listar las fórmulas médicas: " + e.getMessage());
        }
    }
}