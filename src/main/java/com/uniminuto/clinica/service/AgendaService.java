package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.ReprogramarRq;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Agenda (calendario) del medico autenticado. Solo ve y modifica sus propias citas.
 */
public interface AgendaService {

    /** @return citas del medico autenticado en el rango. */
    List<CitaRs> misCitas(LocalDateTime desde, LocalDateTime hasta);

    /** @return detalle de una cita propia. */
    CitaRs consultarCita(Long citaId);

    /** @return respuesta al crear una cita para el medico autenticado. */
    MiRespuestaRS guardarCita(CitaRq citaRq);

    /** @return respuesta al editar una cita propia. */
    MiRespuestaRS actualizarCita(CitaRq citaRq);

    /** @return respuesta al mover una cita propia en el calendario. */
    MiRespuestaRS reprogramar(ReprogramarRq rq);

    /** @return respuesta al cambiar el estado de una cita propia. */
    MiRespuestaRS cambiarEstado(Long citaId, String estado);

    /** @return historial de citas de una mascota. */
    List<CitaRs> historialMascota(Integer mascotaId);

    /** @return horarios del medico autenticado. */
    List<HorarioMedico> misHorarios();

    /** @return Excel con las citas del medico autenticado. */
    byte[] reporteExcel(LocalDate desde, LocalDate hasta);
}
