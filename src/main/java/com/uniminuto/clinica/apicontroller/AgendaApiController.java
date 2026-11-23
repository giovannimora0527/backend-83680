package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AgendaApi;
import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarRq;
import com.uniminuto.clinica.service.AgendaService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador de la agenda del medico, solo delega al servicio.
 */
@RestController
public class AgendaApiController implements AgendaApi {

    @Autowired
    private AgendaService agendaService;

    @Override
    public ResponseEntity<List<CitaRs>> misCitas(LocalDateTime desde, LocalDateTime hasta) {
        return ResponseEntity.ok(agendaService.misCitas(desde, hasta));
    }

    @Override
    public ResponseEntity<CitaRs> consultarCita(Long citaId) {
        return ResponseEntity.ok(agendaService.consultarCita(citaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarCita(CitaRq citaRq) {
        return ResponseEntity.ok(agendaService.guardarCita(citaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCita(CitaRq citaRq) {
        return ResponseEntity.ok(agendaService.actualizarCita(citaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> reprogramar(ReprogramarRq rq) {
        return ResponseEntity.ok(agendaService.reprogramar(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstado(Long citaId, String estado) {
        return ResponseEntity.ok(agendaService.cambiarEstado(citaId, estado));
    }

    @Override
    public ResponseEntity<List<CitaRs>> historialMascota(Integer mascotaId) {
        return ResponseEntity.ok(agendaService.historialMascota(mascotaId));
    }

    @Override
    public ResponseEntity<List<HorarioMedico>> misHorarios() {
        return ResponseEntity.ok(agendaService.misHorarios());
    }

    @Override
    public ResponseEntity<byte[]> reporteExcel(LocalDate desde, LocalDate hasta) {
        return Descarga.archivo(agendaService.reporteExcel(desde, hasta),
                "citas_" + desde + "_" + hasta + ".xlsx", Descarga.EXCEL);
    }
}
