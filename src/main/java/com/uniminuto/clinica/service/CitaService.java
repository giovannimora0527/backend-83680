package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {
    List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;
    MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException;
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;
}
