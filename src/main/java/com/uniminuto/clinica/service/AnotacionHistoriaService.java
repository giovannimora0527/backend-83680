package com.uniminuto.clinica.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;

/**
 * Define las operaciones relacionadas con las anotaciones de historias médicas.
 */
public interface AnotacionHistoriaService {

    /**
     * Obtiene las anotaciones dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return lista de anotaciones encontradas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    List<AnotacionHistoria> obtenerAnotacionesPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    ) throws BadRequestException;

    /**
     * Crea una nueva anotación de historia.
     *
     * @param anotacionHistoriaRq datos de la anotación que se desea crear.
     * @return anotación creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    AnotacionHistoria crearAnotacion(
            AnotacionHistoriaRq anotacionHistoriaRq
    ) throws BadRequestException;

    /**
     * Actualiza una anotación de historia existente.
     *
     * @param id identificador de la anotación.
     * @param anotacionHistoriaRq nuevos datos de la anotación.
     * @return anotación actualizada.
     * @throws BadRequestException si la anotación no existe.
     */
    AnotacionHistoria actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionHistoriaRq
    ) throws BadRequestException;
}