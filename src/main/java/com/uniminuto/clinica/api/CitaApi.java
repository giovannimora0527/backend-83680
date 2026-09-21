package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define el contrato de la API para Citas.
 *
 * @author Alma Hernandez
 */

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")

public interface CitaApi {
    /**
     * Endpoint para filtrar citas dada una fecha inicial
     * y una fecha final, organizadas desde la más reciente hasta la más antigua.
     */
    @GetMapping(value = "/filtrar", produces = {"application/json"})
    ResponseEntity<List<Cita>> filtrarCitasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin)
            throws BadRequestException;

    /**
     * Endpoint para crear una nueva cita en el sistema.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarCita(@RequestBody CitaRq citaRq)
            throws BadRequestException;

    /**
     * Endpoint para actualizar una cita existente en el sistema.
     */
    @PostMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq citaRq)
            throws BadRequestException;
}