package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.model.CitaRq;
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

@RestController
public class CitaApiController implements CitaApi {

    /**
     * Servicio de citas.
     */
    @Autowired
    private CitaService citaService;

    /**
    * Lista todas las citas del sistema.
    *
    * @return listado de citas.
    */
    @Override
    public ResponseEntity<List<Cita>> listarCitas() {
        return ResponseEntity.ok(this.citaService.listarCitas());
    }

    /**
    * Lista las citas dentro de un rango de fechas.
    *
    * @param fechaIni fecha inicial de la consulta.
    * @param fechaFin fecha final de la consulta.
    * @return listado de citas ordenadas de la más reciente a la más antigua.
    */
    @Override
public ResponseEntity<List<Cita>> listarCitasPorFecha(
        LocalDateTime fechaIni, LocalDateTime fechaFin) {
    return ResponseEntity.ok(
            this.citaService.listarCitasPorFecha(fechaIni, fechaFin));
}

    /**
    * Guarda una nueva cita.
    *
    * @param citaRq información de la cita a guardar.
    * @return respuesta del servicio.
    * @throws BadRequestException si los datos de la cita no son válidos.
    */
    @Override
    public ResponseEntity<RespuestaRs> guardarCita(@RequestBody @Valid CitaRq citaRq) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.guardarCita(citaRq));
    }

    /**
    * Actualiza una cita existente.
    *
    * @param citaRq información actualizada de la cita.
    * @return respuesta del servicio.
    * @throws BadRequestException si los datos de la cita no son válidos.
    */
        @Override
    public ResponseEntity<RespuestaRs> actualizarCita(
            @RequestBody @Valid CitaRq citaRq) throws BadRequestException {
        return ResponseEntity.ok(this.citaService.actualizarCita(citaRq));
    }
}
