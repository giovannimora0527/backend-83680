package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface HistoriaMedicaService {
    List<HistoriaMedica> listarHistoriasMedicas() throws BadRequestException;
    MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;
    MiRespuestaRS actualizarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;
    MiRespuestaRS eliminarHistoriaMedica(Long historiaMedicaId) throws BadRequestException;
}
