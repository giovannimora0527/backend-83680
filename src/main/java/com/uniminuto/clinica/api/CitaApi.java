package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {
    /**
     * Lista las citas ordenadas de la más reciente a la más antigua.
     * Si se envía un rango de fechas solo devuelve las citas de ese rango.
     *
     * @param fechaInicial fecha inicial del rango (opcional).
     * @param fechaFinal fecha final del rango (opcional).
     * @return lista de citas.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal) throws BadRequestException;

    /**
     * Consulta una cita por su identificador.
     *
     * @param citaId identificador de la cita.
     * @return cita encontrada.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<Cita> consultarCita(@RequestParam Long citaId) throws BadRequestException;

    /**
     * Lista las citas de una mascota.
     *
     * @param mascotaId identificador de la mascota.
     * @return citas de la mascota.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar-by-mascota", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasPorMascota(@RequestParam Integer mascotaId) throws BadRequestException;

    /**
     * Crea una nueva cita.
     *
     * @param citaRq datos de la cita.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearCita(@RequestBody CitaRq citaRq) throws BadRequestException;

    /**
     * Actualiza los datos de una cita existente según su estado.
     *
     * @param citaRq datos de la cita.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq citaRq) throws BadRequestException;

    /**
     * Reactiva una cita cancelada (operación explícita).
     *
     * @param citaId identificador de la cita.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PatchMapping(value = "/reactivar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> reactivarCita(@RequestParam Long citaId) throws BadRequestException;
}
