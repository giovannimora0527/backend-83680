package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio con la lógica de negocio de las historias clínicas.
 */
public interface HistoriaMedicaService {

    /**
     * Lista las historias de la consulta más reciente a la más antigua.
     *
     * @return lista de historias.
     */
    List<HistoriaMedicaRs> listarHistorias();

    /**
     * Consulta una historia por su id.
     *
     * @param historiaId id de la historia.
     * @return la historia.
     */
    HistoriaMedicaRs consultarHistoria(Long historiaId);

    /**
     * Crea una historia clínica validando cliente, mascota, cita y veterinario.
     *
     * @param historiaRq datos de la historia.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarHistoria(HistoriaMedicaRq historiaRq);
}
