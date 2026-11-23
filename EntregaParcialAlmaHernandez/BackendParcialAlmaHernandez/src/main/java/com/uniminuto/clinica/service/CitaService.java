package com.uniminuto.clinica.service;

import java.time.LocalDateTime;
import java.util.List;

import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.CitaRs;
import com.uniminuto.clinica.model.RespuestaRs;

import org.apache.coyote.BadRequestException;

/**
 * Interfaz que define los servicios relacionados con las citas.
 */
public interface CitaService {

    /**
     * Lista las citas del sistema dentro de un rango de fechas.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de citas dentro del rango de fechas.
     */
    List<CitaRs> listarCitas(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin);

    /**
     * Guarda una nueva cita en el sistema.
     *
     * @param citaRq cita que se desea guardar.
     * @return respuesta del servicio.
     * @throws BadRequestException si existe un error en los datos de la cita.
     */
    RespuestaRs guardarCita(CitaRq citaRq)
            throws BadRequestException;

    /**
     * Actualiza una cita almacenada en el sistema.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return respuesta del servicio.
     * @throws BadRequestException si la cita no existe o
     *         si existe un conflicto de horario.
     */
    RespuestaRs actualizarCita(
            Long id,
            CitaRq citaRq)
            throws BadRequestException;
}