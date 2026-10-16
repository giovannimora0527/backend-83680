package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
/** Controlador que expone los servicios de citas. */
public class CitaApiController implements CitaApi {

    @Autowired
    /** Servicio con la lógica de citas. */
    private  CitaService citaService;

    @Override
    /** Delega la consulta de citas por rango de fechas. */
    public ResponseEntity<List<Cita>> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitasPorFecha(fechaInicial, fechaFinal));
    }

    @Override
    /** Delega la consulta de una cita. */
    public ResponseEntity<Cita> consultarCita(Long citaId) throws BadRequestException {
        return ResponseEntity.ok(citaService.consultarCita(citaId));
    }

    @Override
    /** Delega la consulta de las citas de una mascota. */
    public ResponseEntity<List<Cita>> listarCitasPorMascota(Integer mascotaId) throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitasPorMascota(mascotaId));
    }

    @Override
    /** Delega la creación de una cita y responde 201 (Created). */
    public ResponseEntity<MiRespuestaRS> crearCita(CitaRq citaRq) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearCita(citaRq));
    }

    @Override
    /** Delega la actualización de una cita. */
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq citaRq) throws BadRequestException {
        return ResponseEntity.ok(citaService.actualizarCita(citaRq));
    }

    @Override
    /** Delega la reactivación de una cita cancelada. */
    public ResponseEntity<MiRespuestaRS> reactivarCita(Long citaId) throws BadRequestException {
        return ResponseEntity.ok(citaService.reactivarCita(citaId));
    }
}
