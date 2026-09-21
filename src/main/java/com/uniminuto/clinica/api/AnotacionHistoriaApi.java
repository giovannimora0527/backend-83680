package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
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
 * Rutas de los servicios de las anotaciones de historia medica.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    /**
     * Lista las anotaciones entre dos fechas, de la mas reciente a la mas antigua.
     *
     * @param fechaInicial primer dia del rango (formato yyyy-MM-dd).
     * @param fechaFinal   ultimo dia del rango (formato yyyy-MM-dd).
     * @return las anotaciones del rango.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<AnotacionHistoriaRs>> listarAnotaciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinal);

    /**
     * Guarda una anotacion nueva en una historia medica.
     *
     * @param anotacionRq datos de la anotacion.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarAnotacion(
            @RequestBody AnotacionHistoriaRq anotacionRq);

    /**
     * Actualiza el medico y la descripcion de una anotacion que ya existe.
     *
     * @param anotacionRq datos de la anotacion, incluido su id.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarAnotacion(
            @RequestBody AnotacionHistoriaRq anotacionRq);
}
