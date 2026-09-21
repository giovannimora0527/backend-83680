package com.uniminuto.clinica.service;

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
}
