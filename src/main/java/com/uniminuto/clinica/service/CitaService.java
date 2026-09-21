package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio para la lógica de negocio de Citas.
 * Define el contrato que debe implementar la clase de servicio.
 *
 * @author Alma Hernandez
 */

public interface CitaService {
    /**
     * Obtiene el listado de citas filtradas por un rango de fechas,
     * ordenadas de la más reciente a la más antigua.
     */
    List<Cita> filtrarCitasPorFecha(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Guarda una nueva cita en el sistema.
     */
    MiRespuestaRS guardarCita(CitaRq citaRq) throws BadRequestException;

    /**
     * Actualiza una cita existente en el sistema.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;
}