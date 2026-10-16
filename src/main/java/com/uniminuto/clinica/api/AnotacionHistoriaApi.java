package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {
    /**
     * Registra una nueva anotación en una historia médica abierta.
     *
     * @param anotacionRq datos de la anotación.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearAnotacion(@RequestBody AnotacionHistoriaRq anotacionRq) throws BadRequestException;

    /**
     * Lista las anotaciones creadas entre dos fechas.
     *
     * @param fechaInicial fecha inicial del rango.
     * @param fechaFinal fecha final del rango.
     * @return lista de anotaciones.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal) throws BadRequestException;

    /**
     * Consulta las anotaciones de una historia médica, de la más reciente a la más antigua.
     *
     * @param historiaMedicaId identificador de la historia.
     * @return lista de anotaciones de la historia.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar-by-historia", produces = "application/json")
    ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorHistoria(@RequestParam Long historiaMedicaId)
            throws BadRequestException;

    /**
     * Actualiza una anotación existente.
     *
     * @param anotacionRq datos de la anotación.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarAnotacion(@RequestBody AnotacionHistoriaRq anotacionRq) throws BadRequestException;
}
