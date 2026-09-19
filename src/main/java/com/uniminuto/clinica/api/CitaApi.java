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

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;

/**
 * Define los servicios REST relacionados con las citas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Lista las citas que se encuentran dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return respuesta HTTP con la lista de citas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @GetMapping(value = "/listar-por-fecha",
            produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitasPorFecha(
            @RequestParam("fechaInicial")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicial,

            @RequestParam("fechaFinal")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFinal
    ) throws BadRequestException;

    /**
     * Crea una nueva cita.
     *
     * @param citaRq datos de la cita que se desea crear.
     * @return respuesta HTTP con la cita creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @PostMapping(value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<Cita> crearCita(
            @RequestBody CitaRq citaRq
    ) throws BadRequestException;

    /**
     * Actualiza una cita existente.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return respuesta HTTP con la cita actualizada.
     * @throws BadRequestException si la cita no existe.
     */
    @PutMapping(value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<Cita> actualizarCita(
            @PathVariable Long id,
            @RequestBody CitaRq citaRq
    ) throws BadRequestException;
}