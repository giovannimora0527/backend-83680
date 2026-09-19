package com.uniminuto.clinica.api;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;

/**
 * Define los servicios REST relacionados con las anotaciones
 * de las historias médicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    /**
     * Lista las anotaciones dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return respuesta HTTP con la lista de anotaciones.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/listar-por-fecha",
            produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            @RequestParam("fechaInicial")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicial,

            @RequestParam("fechaFinal")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFinal
    ) throws BadRequestException;

    /**
     * Crea una nueva anotación de historia.
     *
     * @param anotacionHistoriaRq datos de la anotación que se desea crear.
     * @return respuesta HTTP con la anotación creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @PostMapping(value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> crearAnotacion(
            @RequestBody AnotacionHistoriaRq anotacionHistoriaRq
    ) throws BadRequestException;

    /**
     * Actualiza una anotación de historia existente.
     *
     * @param id identificador de la anotación.
     * @param anotacionHistoriaRq nuevos datos de la anotación.
     * @return respuesta HTTP con la anotación actualizada.
     * @throws BadRequestException si la anotación no existe.
     */
    @PutMapping(value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<AnotacionHistoria> actualizarAnotacion(
            @PathVariable Long id,
            @RequestBody AnotacionHistoriaRq anotacionHistoriaRq
    ) throws BadRequestException;
}