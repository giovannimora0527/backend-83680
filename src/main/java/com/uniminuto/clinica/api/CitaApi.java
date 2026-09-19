package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/cita")
public interface CitaApi {

    /**
     * Api para listar todas las citas del sistema.
     * @return listado de citas.
     */
    @RequestMapping(value = "/listar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<List<Cita>> listarCitas();

    /**
     * Api para listar las citas dentro de un rango de fechas.
     * @param fechaIni fecha inicial de la consulta.
     * @param fechaFin fecha final de la consulta.
     * @return listado de citas ordenadas de la más reciente a la más antigua.
     */
    @RequestMapping(value = "/listarPorFecha",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<List<Cita>> listarCitasPorFecha(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime fechaIni,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime fechaFin);

    /**
     * Api para guardar una cita nueva.
     * @param citaRq cita de entrada.
     * @return Respuesta del servicio.
     * @throws BadRequestException excepcion.
     */
    @RequestMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    ResponseEntity<RespuestaRs> guardarCita(
            @RequestBody @Valid CitaRq citaRq) throws BadRequestException;

    /**
     * Api para actualizar una cita existente.
     * @param citaRq información actualizada de la cita.
     * @return respuesta del servicio.
     */
    @RequestMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.PUT)
    ResponseEntity<RespuestaRs> actualizarCita(
            @RequestBody @Valid CitaRq citaRq) throws BadRequestException;
}