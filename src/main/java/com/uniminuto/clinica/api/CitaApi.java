package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * Lista las citas de la mas reciente a la mas antigua.
     * Si no se envian fechas se listan todas.
     *
     * @param fechaInicial primer dia del rango (formato yyyy-MM-dd), opcional.
     * @param fechaFinal   ultimo dia del rango (formato yyyy-MM-dd), opcional.
     * @return las citas.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<CitaRs>> listarCitasPorFechas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinal);

    /**
     * Lista las citas de una mascota (sirve para los combos de historia y formula).
     *
     * @param mascotaId id de la mascota.
     * @return las citas de la mascota.
     */
    @GetMapping(value = "/listar-by-mascota",
            produces = {"application/json"})
    ResponseEntity<List<CitaRs>> listarCitasPorMascota(
            @RequestParam Integer mascotaId);

    /**
     * Consulta una cita por su id.
     *
     * @param citaId id de la cita.
     * @return la cita.
     */
    @GetMapping(value = "/consultar",
            produces = {"application/json"})
    ResponseEntity<CitaRs> consultarCita(
            @RequestParam Long citaId);

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
    @PutMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCita(
            @RequestBody CitaRq citaRq);

    /**
     * Reactiva una cita cancelada (operacion explicita). Queda en estado pendiente.
     *
     * @param citaId id de la cita cancelada.
     * @return respuesta de exito.
     */
    @PutMapping(value = "/reactivar",
            produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> reactivarCita(
            @RequestParam Long citaId);
}
