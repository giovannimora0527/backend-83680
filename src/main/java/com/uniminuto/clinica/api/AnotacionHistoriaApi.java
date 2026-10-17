package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrato REST de las anotaciones de historias clínicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    /**
     * Lista las anotaciones de una historia, de la más reciente a la más antigua.
     * Ejemplo: /anotacion-historia/listar-por-historia/1?page=0&size=5&texto=vacuna
     *
     * @param historiaId id de la historia clínica.
     * @param texto      texto libre (opcional).
     * @param pageable   paginación y orden.
     * @return página de anotaciones.
     */
    @GetMapping(value = "/listar-por-historia/{historiaId}", produces = {"application/json"})
    ResponseEntity<Page<AnotacionHistoria>> listarAnotaciones(
            @PathVariable Long historiaId,
            @RequestParam(required = false) String texto,
            @PageableDefault(size = 5, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable);

    /**
     * Consulta el detalle de una anotación.
     *
     * @param id id de la anotación.
     * @return la anotación (404 si no existe).
     */
    @GetMapping(value = "/consultar/{id}", produces = {"application/json"})
    ResponseEntity<AnotacionHistoria> consultarAnotacion(@PathVariable Long id);

    /**
     * Agrega una anotación a una historia clínica existente.
     *
     * @param anotacionHistoria datos de la anotación.
     * @return mensaje de confirmación (201).
     * @throws BadRequestException si faltan datos.
     */
    @PostMapping(value = "/crear", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(@RequestBody AnotacionHistoriaRq anotacionHistoria)
            throws BadRequestException;
}