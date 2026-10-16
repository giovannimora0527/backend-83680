package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de las citas, solo delega al servicio.
 */
@RestController
public class CitaApiController implements CitaApi {

    /** Servicio con la logica de las citas. */
    @Autowired
    private CitaService citaService;

    @Override
    public ResponseEntity<List<CitaRs>> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        return ResponseEntity.ok(citaService.listarCitasPorFechas(fechaInicial, fechaFinal));
    }

    @Override
    public ResponseEntity<List<CitaRs>> listarCitasPorMascota(Integer mascotaId) {
        return ResponseEntity.ok(citaService.listarCitasPorMascota(mascotaId));
    }

    @Override
    public ResponseEntity<CitaRs> consultarCita(Long citaId) {
        return ResponseEntity.ok(citaService.consultarCita(citaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarCita(CitaRq citaRq) {
        return ResponseEntity.ok(citaService.guardarCita(citaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq citaRq) {
        return ResponseEntity.ok(citaService.actualizarCita(citaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> reactivarCita(Long citaId) {
        return ResponseEntity.ok(citaService.reactivarCita(citaId));
    }
}
