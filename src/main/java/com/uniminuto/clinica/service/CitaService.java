package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio con la lógica de negocio de las citas.
 */
public interface CitaService {

    /**
     * Lista las citas que ocurren entre dos fechas (ambas incluidas),
     * de la más reciente a la más antigua.
     *
     * @param fechaInicial primer día del rango.
     * @param fechaFinal   último día del rango.
     * @return las citas del rango.
     */
    List<CitaRs> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal);

    /**
     * Crea una nueva cita en estado "programada".
     *
     * @param citaRq datos de la cita.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarCita(CitaRq citaRq);

    /**
     * Actualiza una cita existente.
     *
     * @param citaRq datos de la cita, incluido su identificador.
     * @return respuesta de éxito.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq);
}
