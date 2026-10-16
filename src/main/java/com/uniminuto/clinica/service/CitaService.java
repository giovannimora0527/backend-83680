package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/** Define las operaciones de negocio para las citas. */
public interface CitaService {
    /** Lista todas las citas o las de un rango de fechas. */
    List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;

    /** Consulta una cita por su identificador. */
    Cita consultarCita(Long citaId) throws BadRequestException;

    /** Lista las citas de una mascota. */
    List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException;

    /** Crea una cita nueva. */
    MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException;

    /** Actualiza una cita existente según su estado. */
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;

    /** Reactiva una cita cancelada. */
    MiRespuestaRS reactivarCita(Long citaId) throws BadRequestException;
}
