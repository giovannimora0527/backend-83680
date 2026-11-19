package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.BloqueoHorario;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.BloqueoHorarioRq;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cita")
public interface CitaApi {
    /**
     * Lista las citas ordenadas de la más reciente a la más antigua.
     * Permite filtrar opcionalmente por rango de fechas, veterinario y estado.
     *
     * @param fechaInicial fecha inicial del rango (opcional).
     * @param fechaFinal fecha final del rango (opcional).
     * @param medicoId identificador del veterinario (opcional).
     * @param estado estado de la cita (opcional).
     * @return lista de citas.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) String estado) throws BadRequestException;

    /**
     * Consulta una cita por su identificador.
     *
     * @param citaId identificador de la cita.
     * @return cita encontrada.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<Cita> consultarCita(@RequestParam Long citaId) throws BadRequestException;

    /**
     * Lista las citas de una mascota (historial de citas del paciente).
     *
     * @param mascotaId identificador de la mascota.
     * @return citas de la mascota.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar-by-mascota", produces = "application/json")
    ResponseEntity<List<Cita>> listarCitasPorMascota(@RequestParam Integer mascotaId) throws BadRequestException;

    /**
     * Crea una nueva cita validando disponibilidad y bloqueos de horario.
     *
     * @param citaRq datos de la cita.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearCita(@RequestBody CitaRq citaRq) throws BadRequestException;

    /**
     * Actualiza los datos de una cita existente según su estado.
     *
     * @param citaRq datos de la cita.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq citaRq) throws BadRequestException;

    /**
     * Marca una cita como atendida.
     *
     * @param citaId identificador de la cita.
     * @param usuarioCambio perfil o usuario que realiza la acción (opcional).
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PatchMapping(value = "/atender", produces = "application/json")
    ResponseEntity<MiRespuestaRS> marcarCitaAtendida(
            @RequestParam Long citaId,
            @RequestParam(required = false) String usuarioCambio) throws BadRequestException;

    /**
     * Reactiva una cita cancelada (operación explícita).
     *
     * @param citaId identificador de la cita.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PatchMapping(value = "/reactivar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> reactivarCita(@RequestParam Long citaId) throws BadRequestException;

    /**
     * Genera y descarga un archivo Excel (.xlsx) con las citas filtradas por veterinario, rango de fechas y estado.
     *
     * @param fechaInicial fecha inicial del rango (opcional).
     * @param fechaFinal fecha final del rango (opcional).
     * @param medicoId identificador del veterinario asociado (opcional).
     * @param estado estado de las citas (opcional).
     * @return archivo Excel en bytes.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/exportar-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ResponseEntity<byte[]> exportarCitasExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) String estado) throws BadRequestException;

    /**
     * Lista los bloqueos de horario activos, opcionalmente filtrados por veterinario y rango de fechas.
     *
     * @param medicoId identificador del veterinario (opcional).
     * @param fechaInicial fecha inicial (opcional).
     * @param fechaFinal fecha final (opcional).
     * @return lista de bloqueos activos.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/bloqueos", produces = "application/json")
    ResponseEntity<List<BloqueoHorario>> listarBloqueosHorario(
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal) throws BadRequestException;

    /**
     * Registra un bloqueo de horario para un veterinario.
     *
     * @param bloqueoRq datos del bloqueo.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PostMapping(value = "/bloqueos/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearBloqueoHorario(@RequestBody BloqueoHorarioRq bloqueoRq) throws BadRequestException;

    /**
     * Inactiva un bloqueo de horario existente.
     *
     * @param bloqueoId identificador del bloqueo.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PatchMapping(value = "/bloqueos/inactivar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> inactivarBloqueoHorario(@RequestParam Long bloqueoId) throws BadRequestException;
}
