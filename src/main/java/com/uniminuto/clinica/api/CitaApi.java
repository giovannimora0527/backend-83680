package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Rutas de los servicios de las citas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Lista las citas entre dos fechas, de la mas reciente a la mas antigua.
     *
     * @param fechaInicial primer dia del rango (formato yyyy-MM-dd).
     * @param fechaFinal   ultimo dia del rango (formato yyyy-MM-dd).
     * @return las citas del rango.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<CitaRs>> listarCitasPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinal);

    /**
     * Guarda una cita nueva.
     *
     * @param citaRq datos de la cita.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarCita(
            @RequestBody CitaRq citaRq);

    /**
     * Actualiza una cita que ya existe.
     *
     * @param citaRq datos de la cita, incluido su id y su estado.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCita(
            @RequestBody CitaRq citaRq);
}
