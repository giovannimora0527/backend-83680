package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.CitaApi;
import com.uniminuto.clinica.entity.BloqueoHorario;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.BloqueoHorarioRq;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
/** Controlador que expone los servicios de citas, bloqueos de horario y exportación a Excel. */
public class CitaApiController implements CitaApi {

    @Autowired
    /** Servicio con la lógica de citas. */
    private CitaService citaService;

    @Override
    /** Delega la consulta de citas por rango de fechas, veterinario y estado. */
    public ResponseEntity<List<Cita>> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal,
                                                          Long medicoId, String estado) throws BadRequestException {
        return ResponseEntity.ok(citaService.listarCitasPorFecha(fechaInicial, fechaFinal, medicoId, estado));
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
    /** Delega la marcación de una cita como atendida. */
    public ResponseEntity<MiRespuestaRS> marcarCitaAtendida(Long citaId, String usuarioCambio) throws BadRequestException {
        return ResponseEntity.ok(citaService.marcarCitaAtendida(citaId, usuarioCambio));
    }

    @Override
    /** Delega la reactivación de una cita cancelada. */
    public ResponseEntity<MiRespuestaRS> reactivarCita(Long citaId) throws BadRequestException {
        return ResponseEntity.ok(citaService.reactivarCita(citaId));
    }

    @Override
    /** Genera el archivo Excel con las citas filtradas y lo devuelve como descarga. */
    public ResponseEntity<byte[]> exportarCitasExcel(LocalDateTime fechaInicial, LocalDateTime fechaFinal,
                                                     Long medicoId, String estado) throws BadRequestException {
        byte[] archivo = citaService.exportarCitasExcel(fechaInicial, fechaFinal, medicoId, estado);
        String nombreArchivo = "reporte_citas_" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(archivo);
    }

    @Override
    /** Lista los bloqueos de horario activos según veterinario y rango de fechas. */
    public ResponseEntity<List<BloqueoHorario>> listarBloqueosHorario(Long medicoId, LocalDateTime fechaInicial,
                                                                      LocalDateTime fechaFinal) throws BadRequestException {
        return ResponseEntity.ok(citaService.listarBloqueosHorario(medicoId, fechaInicial, fechaFinal));
    }

    @Override
    /** Registra un nuevo bloqueo de horario para un veterinario. */
    public ResponseEntity<MiRespuestaRS> crearBloqueoHorario(BloqueoHorarioRq bloqueoRq) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crearBloqueoHorario(bloqueoRq));
    }

    @Override
    /** Inactiva un bloqueo de horario existente. */
    public ResponseEntity<MiRespuestaRS> inactivarBloqueoHorario(Long bloqueoId) throws BadRequestException {
        return ResponseEntity.ok(citaService.inactivarBloqueoHorario(bloqueoId));
    }
}
