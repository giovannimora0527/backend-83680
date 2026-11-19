package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.BloqueoHorario;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.BloqueoHorarioRq;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDateTime;
import java.util.List;

/** Define las operaciones de negocio para las citas, calendario y bloqueos de horario. */
public interface CitaService {
    /** Lista todas las citas o las de un rango de fechas. */
    List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;

    /** Lista las citas aplicando filtros opcionales de rango de fechas, veterinario y estado. */
    List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal,
                                   Long medicoId, String estado) throws BadRequestException;

    /** Consulta una cita por su identificador. */
    Cita consultarCita(Long citaId) throws BadRequestException;

    /** Lista las citas de una mascota. */
    List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException;

    /** Crea una cita nueva. */
    MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException;

    /** Actualiza una cita existente según su estado. */
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;

    /** Marca una cita como atendida. */
    MiRespuestaRS marcarCitaAtendida(Long citaId, String usuarioCambio) throws BadRequestException;

    /** Reactiva una cita cancelada. */
    MiRespuestaRS reactivarCita(Long citaId) throws BadRequestException;

    /** Genera el reporte Excel (.xlsx) de citas filtradas por veterinario, fechas y estado. */
    byte[] exportarCitasExcel(LocalDateTime fechaInicial, LocalDateTime fechaFinal,
                              Long medicoId, String estado) throws BadRequestException;

    /** Lista los bloqueos de horario activos filtrados opcionalmente por veterinario y fechas. */
    List<BloqueoHorario> listarBloqueosHorario(Long medicoId, LocalDateTime fechaInicial,
                                               LocalDateTime fechaFinal) throws BadRequestException;

    /** Registra un nuevo bloqueo de horario en la agenda de un veterinario. */
    MiRespuestaRS crearBloqueoHorario(BloqueoHorarioRq bloqueoRq) throws BadRequestException;

    /** Inactiva un bloqueo de horario existente. */
    MiRespuestaRS inactivarBloqueoHorario(Long bloqueoId) throws BadRequestException;
}
