package com.uniminuto.clinica.api;

import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API para los servicios relacionados con las historias médicas.
 */
@CrossOrigin(origins = "*")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    /**
     * Crea una nueva historia médica.
     *
     * @param historiaRq datos necesarios para crear la historia.
     * @return respuesta del servicio.
     * @throws BadRequestException si los datos no son válidos.
     */
    @RequestMapping(
            value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    ResponseEntity<RespuestaRs> guardarHistoria(
            @RequestBody @Valid HistoriaMedicaRq historiaRq)
            throws BadRequestException;

    /**
     * Lista las historias médicas dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de historias médicas.
     */
    @RequestMapping(
            value = "/listar",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<List<HistoriaMedicaRs>> listarHistorias(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime fechaIni,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime fechaFin);

    /**
     * Actualiza una anotación de una historia médica.
     *
     * @param id identificador de la anotación que se desea actualizar.
     * @param anotacionRq nuevos datos de la anotación.
     * @return respuesta del servicio.
     * @throws BadRequestException si la anotación no existe.
     */
    @RequestMapping(
            value = "/anotacion/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    ResponseEntity<RespuestaRs> actualizarAnotacion(
            @RequestParam Long id,
            @RequestBody @Valid AnotacionHistoriaRq anotacionRq)
            throws BadRequestException;
}