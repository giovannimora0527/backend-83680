package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Especializacion;

import java.util.List;

/**
 * Servicio de las especializaciones.
 */
public interface EspecializacionService {

    /**
     * Lista las especializaciones ordenadas por nombre.
     *
     * @return lista de especializaciones.
     */
    List<Especializacion> listarEspecializaciones();
}
