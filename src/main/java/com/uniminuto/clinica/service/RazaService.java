package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Raza;
import java.util.List;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;

/**
 * Servicio con la lógica de negocio de las razas.
 */
public interface RazaService {

    /**
     * Crea una nueva raza.
     *
     * @param razaRq datos de la raza.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarRazaNueva(RazaRq razaRq);

    /**
     * Lista todas las razas ordenadas por nombre.
     *
     * @return lista de razas.
     */
    List<Raza> listarRazas();
}
