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
 * Controlador de los servicios de las citas.
 */
@RestController
public class CitaApiController implements CitaApi {

    /**
     * Servicio con la logica de las citas.
     */
    @Autowired
    private CitaService citaService;

    /**
     * Lista las citas entre dos fechas.
     *
     * @param fechaInicial primer dia del rango.
     * @param fechaFinal   ultimo dia del rango.
     * @return las citas del rango.
     */
    @Override
    public ResponseEntity<List<CitaRs>> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        return ResponseEntity.ok(citaService.listarCitasPorFechas(fechaInicial, fechaFinal));
    }

    /**
     * Guarda una cita nueva.
     *
     * @param citaRq datos de la cita.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> guardarCita(CitaRq citaRq) {
        return ResponseEntity.ok(citaService.guardarCita(citaRq));
    }

    /**
     * Actualiza una cita que ya existe.
     *
     * @param citaRq datos de la cita.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq citaRq) {
        return ResponseEntity.ok(citaService.actualizarCita(citaRq));
    }
}
