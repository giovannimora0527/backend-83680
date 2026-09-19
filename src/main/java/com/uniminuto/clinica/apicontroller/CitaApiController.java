package com.uniminuto.clinica.apicontroller;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.service.CitaService;

/**
 * Controlador REST encargado de gestionar las solicitudes
 * relacionadas con las citas.
 */
@RestController
public class CitaApiController implements CitaApi {

    @Autowired
    private CitaService citaService;

    /**
     * Lista las citas dentro de un rango de fechas.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return respuesta HTTP con las citas encontradas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public ResponseEntity<List<Cita>> listarCitasPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal) throws BadRequestException {

        List<Cita> citas = citaService.obtenerCitasPorFecha(
                fechaInicial,
                fechaFinal
        );

        return ResponseEntity.ok(citas);
    }

    /**
     * Crea una nueva cita.
     *
     * @param citaRq datos de la cita que se desea crear.
     * @return respuesta HTTP con la cita creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public ResponseEntity<Cita> crearCita(CitaRq citaRq)
            throws BadRequestException {

        Cita cita = citaService.crearCita(citaRq);

        return ResponseEntity.ok(cita);
    }

    /**
     * Actualiza una cita existente.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return respuesta HTTP con la cita actualizada.
     * @throws BadRequestException si la cita no existe.
     */
    @Override
    public ResponseEntity<Cita> actualizarCita(
            Long id,
            CitaRq citaRq) throws BadRequestException {

        Cita cita = citaService.actualizarCita(id, citaRq);

        return ResponseEntity.ok(cita);
    }
}