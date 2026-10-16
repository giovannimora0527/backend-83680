package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.MedicoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio con la logica de negocio de los medicos.
 */
public interface MedicoService {

    /**
     * Lista todos los medicos ordenados por nombre.
     *
     * @return lista de medicos.
     */
    List<Medico> listarMedicos();

    /**
     * Crea un medico validando que el documento y el registro no esten repetidos.
     *
     * @param medicoRq datos del medico.
     * @return respuesta de exito.
     */
    MiRespuestaRS guardarMedico(MedicoRq medicoRq);

    /**
     * Actualiza un medico existente.
     *
     * @param medicoRq datos del medico con su id.
     * @return respuesta de exito.
     */
    MiRespuestaRS actualizarMedico(MedicoRq medicoRq);
}
