package com.uniminuto.clinica.service;

import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define los servicios relacionados
 * con las historias médicas.
 */
public interface HistoriaMedicaService {

    /**
     * Crea una nueva historia médica.
     *
     * @param historiaRq datos necesarios para crear la historia.
     * @return respuesta del servicio.
     * @throws BadRequestException si los datos no son válidos.
     */
    RespuestaRs guardarHistoria(
            HistoriaMedicaRq historiaRq)
            throws BadRequestException;

    /**
     * Lista las historias médicas dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de historias médicas.
     */
    List<HistoriaMedicaRs> listarHistorias(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin);

    /**
     * Actualiza una anotación de una historia médica.
     *
     * @param id identificador de la anotación que se desea actualizar.
     * @param anotacionRq nuevos datos de la anotación.
     * @return respuesta del servicio.
     * @throws BadRequestException si la anotación no existe.
     */
    RespuestaRs actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionRq)
            throws BadRequestException;
}