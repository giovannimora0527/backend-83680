package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST de citas. Delega toda la logica en {@link CitaService}.
 */
@RestController
public class CitaApiController implements CitaApi {

    @Autowired
    private CitaService citaService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Page<Cita>> listarCitas(String estado, Long medicoId, LocalDateTime desde,
                                                  LocalDateTime hasta, Pageable pageable)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitas(estado, medicoId, desde, hasta, pageable));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<List<Cita>> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitasRango(fechaInicio, fechaFin));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Cita> consultarCita(Long id) {
        return ResponseEntity.ok(citaService.consultarCita(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<MiRespuestaRS> crearCita(CitaRq cita) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCita(cita));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq cita) throws BadRequestException {
        return ResponseEntity.ok(citaService.actualizarCita(cita));
    }
}