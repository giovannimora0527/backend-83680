package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;

import java.util.List;

/**
 * Interfaz del servicio para la lógica de negocio de Fórmulas Médicas.
 * Define el contrato que debe implementar la clase de servicio.
 *
 * @author Alma Hernandez
 */

public interface FormulaMedicaService {
    /**
     * Obtiene el listado de fórmulas médicas ordenadas por fecha de creación
     * de la más reciente a la más antigua.
     */
    List<FormulaMedica> listarFormulasMedicas() throws BadRequestException;
}