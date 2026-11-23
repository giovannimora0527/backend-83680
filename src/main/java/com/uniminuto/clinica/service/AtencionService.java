package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.AtencionInfoRs;
import com.uniminuto.clinica.models.AtencionRq;
import com.uniminuto.clinica.models.AtencionRs;

/**
 * Atencion de una cita por parte del medico: historia clinica, formula y prefactura.
 */
public interface AtencionService {

    /**
     * Datos para atender una cita propia.
     *
     * @param citaId id de la cita.
     * @return informacion de la cita, antecedentes y servicios.
     */
    AtencionInfoRs consultarAtencion(Long citaId);

    /**
     * Registra la atencion, genera la formula (si hay medicamentos) y marca la cita como atendida.
     *
     * @param rq datos de la atencion.
     * @return ids generados y advertencias.
     */
    AtencionRs finalizarAtencion(AtencionRq rq);

    /**
     * Prefactura en PDF con el servicio prestado y los medicamentos formulados.
     *
     * @param citaId id de la cita atendida.
     * @return bytes del PDF.
     */
    byte[] prefacturaPdf(Long citaId);
}
