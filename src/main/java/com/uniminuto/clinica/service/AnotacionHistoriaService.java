package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio para la lógica de negocio de Anotaciones de Historia Médica.
 *
 * @author Alma Hernandez
 */

public interface AnotacionHistoriaService {
    /**
     * Lista anotaciones filtradas por un rango de fechas, ordenadas descendientemente.
     */
    List<AnotacionHistoria> listarAnotacionesPorFecha(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Crea una nueva anotación en la historia médica.
     */
    MiRespuestaRS crearAnotacion(AnotacionHistoriaRq rq) throws BadRequestException;

    /**
     * Actualiza una anotación existente.
     */
    MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq rq) throws BadRequestException;
}