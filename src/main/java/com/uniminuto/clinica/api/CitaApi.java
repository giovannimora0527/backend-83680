package com.uniminuto.clinica.api;

import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.CitaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * API que expone los servicios relacionados con las citas.
 */
@CrossOrigin(origins = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * API para listar las citas del sistema dentro de un rango de fechas.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return listado de citas dentro del rango.
     */
    @RequestMapping(value = "/listar",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<List<CitaRs>> listarCitas(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime fechaIni,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime fechaFin
    );

    /**
     * API para guardar una cita nueva.
     *
     * @param citaRq cita de entrada.
     * @return respuesta del servicio.
     * @throws BadRequestException excepción.
     */
    @RequestMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    ResponseEntity<RespuestaRs> guardarCita(
            @RequestBody @Valid CitaRq citaRq
    ) throws BadRequestException;

    /**
     * API para actualizar una cita existente.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return respuesta del servicio.
     * @throws BadRequestException excepción.
     */
    @RequestMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    ResponseEntity<RespuestaRs> actualizarCita(
            @RequestParam Long id,
            @RequestBody @Valid CitaRq citaRq
    ) throws BadRequestException;
}