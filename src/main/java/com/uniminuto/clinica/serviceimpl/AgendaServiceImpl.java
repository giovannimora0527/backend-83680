package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarRq;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.AgendaService;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Agenda del medico: reutiliza las reglas de CitaService y agrega la validacion
 * de que la cita pertenezca al medico que inicio sesion.
 */
@Service
public class AgendaServiceImpl implements AgendaService {

    @Autowired
    private CitaService citaService;

    @Override
    public List<CitaRs> misCitas(LocalDateTime desde, LocalDateTime hasta) {
        return citaService.listarCitasMedico(UsuarioActual.medicoId(), desde, hasta);
    }

    @Override
    public CitaRs consultarCita(Long citaId) {
        return citaService.convertirCita(this.citaPropia(citaId));
    }

    @Override
    public MiRespuestaRS guardarCita(CitaRq citaRq) {
        // El medico solo agenda citas para si mismo
        citaRq.setMedicoId(UsuarioActual.medicoId());
        return citaService.guardarCita(citaRq);
    }

    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq) {
        this.citaPropia(citaRq.getCitaId());
        citaRq.setMedicoId(UsuarioActual.medicoId());
        return citaService.actualizarCita(citaRq);
    }

    @Override
    public MiRespuestaRS reprogramar(ReprogramarRq rq) {
        if (rq == null) {
            throw new BadRequestException("Datos obligatorios");
        }
        this.citaPropia(rq.getCitaId());
        return citaService.reprogramarCita(rq.getCitaId(), rq.getFechaHora(), rq.getFechaHoraFin());
    }

    @Override
    public MiRespuestaRS cambiarEstado(Long citaId, String estado) {
        this.citaPropia(citaId);
        return citaService.cambiarEstado(citaId, estado);
    }

    @Override
    public List<CitaRs> historialMascota(Integer mascotaId) {
        return citaService.listarCitasPorMascota(mascotaId);
    }

    @Override
    public List<HorarioMedico> misHorarios() {
        return citaService.listarHorarios(UsuarioActual.medicoId());
    }

    @Override
    public byte[] reporteExcel(LocalDate desde, LocalDate hasta) {
        return citaService.reporteCitasExcel(UsuarioActual.medicoId(), desde, hasta);
    }

    /**
     * Busca la cita y valida que sea del medico autenticado.
     *
     * @param citaId id de la cita.
     * @return la cita.
     */
    private Cita citaPropia(Long citaId) {
        Cita cita = citaService.buscarCita(citaId);
        if (!cita.getMedico().getId().equals(UsuarioActual.medicoId())) {
            throw new BadRequestException("Esa cita no está asignada a usted", HttpStatus.FORBIDDEN);
        }
        return cita;
    }
}
