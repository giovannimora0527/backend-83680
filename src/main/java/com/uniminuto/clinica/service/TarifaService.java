package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.TarifaLogRs;
import com.uniminuto.clinica.models.TarifaRq;

import java.util.List;

/**
 * Gestion de tarifas por tipo de servicio y especialidad.
 */
public interface TarifaService {

    /** @return todas las tarifas. */
    List<Tarifa> listarTarifas();

    /** @return respuesta al crear. */
    MiRespuestaRS guardarTarifa(TarifaRq rq);

    /** @return respuesta al editar (registra el log). */
    MiRespuestaRS actualizarTarifa(TarifaRq rq);

    /** @return respuesta al activar o inactivar. */
    MiRespuestaRS cambiarEstado(Long id, Boolean activo);

    /** @return historial de cambios de una tarifa. */
    List<TarifaLogRs> listarLog(Long tarifaId);

    /**
     * Busca una tarifa activa.
     *
     * @param id id.
     * @return tarifa.
     */
    Tarifa buscarActiva(Long id);
}
