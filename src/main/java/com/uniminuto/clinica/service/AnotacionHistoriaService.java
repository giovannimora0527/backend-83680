package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface AnotacionHistoriaService {
    MiRespuestaRS crearAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException;
    List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;
    MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException;
}
