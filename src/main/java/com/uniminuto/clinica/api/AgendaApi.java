package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarRq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Rutas de la agenda (calendario) del medico autenticado. Solo perfil Medico.
 */
@Tag(name = "Agenda del médico", description = "Calendario de citas del veterinario autenticado")
@RequestMapping("/agenda")
public interface AgendaApi {

    @Operation(summary = "Citas del médico autenticado en un rango (día, semana o mes)")
    @GetMapping(value = "/citas", produces = "application/json")
    ResponseEntity<List<CitaRs>> misCitas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta);

    @Operation(summary = "Detalle de una cita propia (propietario, paciente, motivo, horario)")
    @GetMapping(value = "/cita", produces = "application/json")
    ResponseEntity<CitaRs> consultarCita(@RequestParam Long citaId);

    @Operation(summary = "Registrar una cita para el médico autenticado")
    @PostMapping(value = "/cita", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> guardarCita(@RequestBody CitaRq citaRq);

    @Operation(summary = "Editar una cita propia")
    @PutMapping(value = "/cita", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarCita(@RequestBody CitaRq citaRq);

    @Operation(summary = "Cambiar fecha y hora de una cita (arrastrar en el calendario)")
    @PutMapping(value = "/reprogramar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> reprogramar(@RequestBody ReprogramarRq rq);

    @Operation(summary = "Cambiar el estado de una cita propia (confirmada, atendida, cancelada)")
    @PutMapping(value = "/estado", produces = "application/json")
    ResponseEntity<MiRespuestaRS> cambiarEstado(@RequestParam Long citaId, @RequestParam String estado);

    @Operation(summary = "Historial de citas de una mascota")
    @GetMapping(value = "/historial-mascota", produces = "application/json")
    ResponseEntity<List<CitaRs>> historialMascota(@RequestParam Integer mascotaId);

    @Operation(summary = "Horarios de disponibilidad del médico autenticado")
    @GetMapping(value = "/horarios", produces = "application/json")
    ResponseEntity<List<HorarioMedico>> misHorarios();

    @Operation(summary = "Reporte Excel (.xlsx) de las citas del médico entre dos fechas")
    @GetMapping(value = "/reporte-excel")
    ResponseEntity<byte[]> reporteExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta);
}
