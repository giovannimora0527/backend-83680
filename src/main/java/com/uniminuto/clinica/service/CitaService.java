package com.uniminuto.clinica.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;

/**
 * Define las operaciones relacionadas con las citas de la clínica.
 */
public interface CitaService {

    /**
     * Obtiene las citas que se encuentran dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return lista de citas encontradas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    List<Cita> obtenerCitasPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    ) throws BadRequestException;

    /**
     * Crea una nueva cita.
     *
     * @param citaRq datos de la cita que se desea crear.
     * @return cita creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    Cita crearCita(CitaRq citaRq) throws BadRequestException;

    /**
     * Actualiza una cita existente.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return cita actualizada.
     * @throws BadRequestException si la cita no existe.
     */
    Cita actualizarCita(
            Long id,
            CitaRq citaRq
    ) throws BadRequestException;

}