package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.CitaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.CitaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador que expone los servicios relacionados con las citas.
 */
@RestController
public class CitaApiController implements CitaApi {

    /**
     * Servicio de citas.
     */
    @Autowired
    private CitaService citaService;

    /**
     * Lista las citas dentro de un rango de fechas.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de citas dentro del rango solicitado.
     */
    @Override
    public ResponseEntity<List<CitaRs>> listarCitas(
            LocalDateTime fechaIni, LocalDateTime fechaFin) {
        return ResponseEntity.ok(
                this.citaService.listarCitas(fechaIni, fechaFin));
    }

    /**
     * Guarda una nueva cita en el sistema.
     *
     * @param citaRq cita de entrada.
     * @return respuesta del servicio.
     * @throws BadRequestException excepción.
     */
    @Override
    public ResponseEntity<RespuestaRs> guardarCita(
            @RequestBody @Valid CitaRq citaRq) throws BadRequestException {
        return ResponseEntity.ok(
                this.citaService.guardarCita(citaRq));
    }

    /**
     * Actualiza una cita existente en el sistema.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return respuesta del servicio.
     * @throws BadRequestException excepción.
     */
    @Override
    public ResponseEntity<RespuestaRs> actualizarCita(
            Long id, @RequestBody @Valid CitaRq citaRq)
            throws BadRequestException {

        return ResponseEntity.ok(
                this.citaService.actualizarCita(id, citaRq));
    }
}