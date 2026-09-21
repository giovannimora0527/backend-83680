package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST que implementa la API de Anotaciones de Historia Médica.
 *
 * @author Alma Hernandez
 */

@RestController

public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {
    /**
     * Lista las anotaciones médicas dentro de un rango de fechas.
     */
    @Autowired
    private AnotacionHistoriaService service;

    @Override
    public ResponseEntity<List<AnotacionHistoria>> listarAnotaciones(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin)
            throws BadRequestException {
        try {
            return ResponseEntity.ok(service.listarAnotacionesPorFecha(fechaInicio, fechaFin));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al listar: " + e.getMessage());
        }
    }

    /**
     * Guarda una nueva anotación en la historia médica.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> guardarAnotacion(@RequestBody AnotacionHistoriaRq rq) 
            throws BadRequestException {
        try {
            return ResponseEntity.ok(service.crearAnotacion(rq));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al guardar: " + e.getMessage());
        }
    }

    /**
     * Actualiza una anotación existente en la historia médica.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarAnotacion(@RequestBody AnotacionHistoriaRq rq) 
            throws BadRequestException {
        try {
            return ResponseEntity.ok(service.actualizarAnotacion(rq));
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al actualizar: " + e.getMessage());
        }
    }
}