package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

/** Define las operaciones de negocio para las anotaciones médicas. */
public interface AnotacionHistoriaService {
    /** Crea una anotación. */
    MiRespuestaRS crearAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException;

    /** Lista anotaciones dentro de un rango de fechas. */
    List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException;

    /** Lista las anotaciones de una historia médica. */
    List<AnotacionHistoria> listarAnotacionesPorHistoria(Long historiaMedicaId) throws BadRequestException;

    /** Actualiza una anotación existente. */
    MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException;
}
