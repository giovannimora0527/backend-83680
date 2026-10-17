package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.HistoriaClinicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

/**
 * Contrato REST de historias clínicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia-clinica")
public interface HistoriaClinicaApi {

    /**
     * Lista historias clínicas paginadas, por defecto ordenadas por fecha y hora descendente.
     * Ejemplo: /historia-clinica/listar?page=0&size=10&texto=firulais&estado=ABIERTA
     *
     * @param texto     texto libre (mascota, cliente, veterinario o motivo), opcional.
     * @param estado    filtro por estado (opcional).
     * @param clienteId filtro por cliente (opcional).
     * @param mascotaId filtro por mascota (opcional).
     * @param medicoId  filtro por veterinario (opcional).
     * @param desde     fecha mínima (opcional, formato ISO).
     * @param hasta     fecha máxima (opcional, formato ISO).
     * @param pageable  paginación y orden.
     * @return página de historias clínicas.
     * @throws BadRequestException si algún filtro no es válido.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<Page<HistoriaMedica>> listarHistorias(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Integer mascotaId,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @PageableDefault(size = 10, sort = "fechaHora", direction = Sort.Direction.DESC) Pageable pageable)
            throws BadRequestException;

    /**
     * Consulta una historia clínica por id.
     *
     * @param id id de la historia clínica.
     * @return la historia (404 si no existe).
     */
    @GetMapping(value = "/consultar/{id}", produces = {"application/json"})
    ResponseEntity<HistoriaMedica> consultarHistoria(@PathVariable Long id);

    /**
     * Crea una historia clínica.
     *
     * @param historia datos de la historia.
     * @return mensaje de confirmación (201).
     * @throws BadRequestException si faltan datos.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearHistoria(@RequestBody HistoriaClinicaRq historia)
            throws BadRequestException;
}