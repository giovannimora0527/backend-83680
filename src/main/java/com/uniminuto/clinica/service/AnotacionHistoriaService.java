package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface AnotacionHistoriaService {

    List<AnotacionHistoria> listarAnotaciones()
            throws BadRequestException;

    List<AnotacionHistoria> listarPorRango(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    ) throws BadRequestException;

    AnotacionHistoria crearAnotacion(
            AnotacionHistoria anotacion
    ) throws BadRequestException;

    AnotacionHistoria actualizarAnotacion(
            Long id,
            AnotacionHistoria anotacion
    ) throws BadRequestException;
}