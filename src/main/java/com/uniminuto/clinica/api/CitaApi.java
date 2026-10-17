package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato REST de citas medicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Lista citas paginadas, por defecto ordenadas por fecha y hora descendente.
     * Ejemplo: /cita/listar?page=0&size=10&sort=fechaHora,desc&estado=PENDIENTE
     *
     * @param estado   filtro por estado (opcional).
     * @param medicoId filtro por veterinario (opcional).
     * @param desde    fecha minima (opcional, formato ISO).
     * @param hasta    fecha maxima (opcional, formato ISO).
     * @param pageable paginacion y orden.
     * @return pagina de citas.
     * @throws BadRequestException si algun filtro no es valido.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<Page<Cita>> listarCitas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @PageableDefault(size = 10, sort = "fechaHora", direction = Sort.Direction.DESC) Pageable pageable)
            throws BadRequestException;

    /**
     * Lista las citas de un rango de fechas.
     *
     * @param fechaInicio inicio del rango.
     * @param fechaFin    fin del rango.
     * @return citas del rango.
     * @throws BadRequestException si el rango no es valido.
     */
    @GetMapping(value = "/listar-rango", produces = {"application/json"})
    ResponseEntity<List<Cita>> listarCitasRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin)
            throws BadRequestException;

    /**
     * Consulta una cita por id.
     *
     * @param id id de la cita.
     * @return la cita (404 si no existe).
     */
    @GetMapping(value = "/consultar/{id}", produces = {"application/json"})
    ResponseEntity<Cita> consultarCita(@PathVariable Long id);

    /**
     * Crea una cita.
     *
     * @param cita datos de la cita.
     * @return mensaje de confirmacion (201).
     * @throws BadRequestException si faltan datos.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearCita(@RequestBody CitaRq cita) throws BadRequestException;

    /**
     * Actualiza una cita existente (el id va en el cuerpo).
     *
     * @param cita datos de la cita.
     * @return mensaje de confirmacion.
     * @throws BadRequestException si faltan datos.
     */
    @PutMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq cita) throws BadRequestException;
}