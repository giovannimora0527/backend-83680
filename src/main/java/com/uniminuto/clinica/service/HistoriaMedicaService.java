package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface HistoriaMedicaService {

    List<HistoriaMedica> listarHistorias()
            throws BadRequestException;

    HistoriaMedica crearHistoria(
            HistoriaMedica historia
    ) throws BadRequestException;

    HistoriaMedica actualizarHistoria(
            Long id,
            HistoriaMedica historia
    ) throws BadRequestException;
}