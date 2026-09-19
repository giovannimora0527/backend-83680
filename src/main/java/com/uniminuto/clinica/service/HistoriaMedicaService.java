package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.util.List;

/** Define las operaciones de negocio para historias médicas. */
public interface HistoriaMedicaService {
    /** Lista las historias médicas. */
    List<HistoriaMedica> listarHistoriasMedicas() throws BadRequestException;
    MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;
    MiRespuestaRS actualizarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;
    MiRespuestaRS eliminarHistoriaMedica(Long historiaMedicaId) throws BadRequestException;
}
    /** Crea una historia médica. */
    /** Actualiza una historia médica. */
    /** Elimina una historia médica. */
