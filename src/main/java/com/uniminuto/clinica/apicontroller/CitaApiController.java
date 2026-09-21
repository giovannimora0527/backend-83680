package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST que implementa la API de Citas.
 * Expone los endpoints para filtrar, crear y actualizar citas.
 *
 * @author Alma Hernandez
 */

@RestController

public class CitaApiController implements CitaApi {
    /**
     * Servicio inyectado para la lógica de negocio de citas.
     */
    @Autowired
    private CitaService citaService;

    /**
     * Filtra las citas dentro de un rango de fechas específico.
     */
    @Override
    public ResponseEntity<List<Cita>> filtrarCitasPorFecha(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin)
            throws BadRequestException {
        try {
            List<Cita> citas = citaService.filtrarCitasPorFecha(fechaInicio, fechaFin);
            return ResponseEntity.ok(citas);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al filtrar las citas: " + e.getMessage());
        }
    }

    /**
     * Guarda una nueva cita en el sistema.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> guardarCita(@RequestBody CitaRq citaRq) throws BadRequestException {
        try {
            MiRespuestaRS respuesta = citaService.guardarCita(citaRq);
            return ResponseEntity.ok(respuesta);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al guardar la cita: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de una cita existente.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq citaRq) throws BadRequestException {
        try {
            MiRespuestaRS respuesta = citaService.actualizarCita(citaRq);
            return ResponseEntity.ok(respuesta);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al actualizar la cita: " + e.getMessage());
        }
    }
}