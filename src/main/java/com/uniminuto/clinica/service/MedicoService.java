package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;

import java.util.List;

/**
 * Servicio con la logica de los medicos.
 */
public interface MedicoService {

    /**
     * Lista los medicos de la clinica.
     *
     * @return los medicos registrados.
     */
    List<Medico> listarMedicos();
}
