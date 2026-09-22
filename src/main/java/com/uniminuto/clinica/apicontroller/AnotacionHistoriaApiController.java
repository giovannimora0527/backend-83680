package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestionar los endpoints de Anotación de Historia.
 * Permite crear, actualizar y filtrar anotaciones por rangos de fecha[cite: 13].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@RestController
@RequestMapping("/api/anotacion-historia")
public class AnotacionHistoriaApiController {

    /** Servicio encargado de la lógica de negocio para las anotaciones de historia[cite: 13]. */
    @Autowired
    private AnotacionHistoriaService anotacionHistoriaService;

    /**
     * Requerimiento 5a: Crea una nueva anotación en el sistema[cite: 13].
     *
     * @param anotacion Objeto de tipo AnotacionHistoria enviado en el cuerpo de la petición[cite: 13].
     * @return ResponseEntity con la anotación creada y el estado HTTP CREATED[cite: 13].
     */
    @PostMapping
    public ResponseEntity<AnotacionHistoria> crearAnotacion(@RequestBody AnotacionHistoria anotacion) {
        return new ResponseEntity<>(anotacionHistoriaService.guardarAnotacion(anotacion), HttpStatus.CREATED);
    }

    /**
     * Requerimiento 5b: Actualiza una anotación existente basada en su identificador[cite: 13].
     *
     * @param id Identificador único de la anotación a actualizar[cite: 13].
     * @param anotacion Objeto con los nuevos datos de la anotación[cite: 13].
     * @return ResponseEntity con la anotación actualizada y el estado HTTP OK[cite: 13].
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnotacionHistoria> actualizarAnotacion(@PathVariable("id") Long id, @RequestBody AnotacionHistoria anotacion) {
        return ResponseEntity.ok(anotacionHistoriaService.actualizarAnotacion(id, anotacion));
    }

    /**
     * Requerimiento 5c: Lista las anotaciones filtradas por un rango de fechas y ordenadas de forma descendente[cite: 13].
     *
     * @param fechaInicio Fecha y hora inicial del filtro[cite: 13].
     * @param fechaFin Fecha y hora final del filtro[cite: 13].
     * @return ResponseEntity con la lista de anotaciones filtradas y ordenadas[cite: 13].
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<AnotacionHistoria>> filtrarAnotaciones(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(anotacionHistoriaService.filtrarPorRangoFechas(fechaInicio, fechaFin));
    }
}