package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.FormulaMedicaRs;

import java.util.List;

/**
 * Servicio con la lógica de negocio de las fórmulas médicas.
 */
public interface FormulaMedicaService {

    /**
     * Lista las fórmulas médicas ordenadas por fecha de creación,
     * de la más reciente a la más antigua.
     *
     * @return las fórmulas médicas registradas.
     */
    List<FormulaMedicaRs> listarFormulasMedicas();
}
