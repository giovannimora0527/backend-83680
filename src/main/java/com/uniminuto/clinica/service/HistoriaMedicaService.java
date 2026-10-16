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

    /** Consulta una historia médica por su identificador. */
    HistoriaMedica consultarHistoriaMedica(Long historiaMedicaId) throws BadRequestException;

    /** Crea una historia médica. */
    MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;

    /** Actualiza una historia médica. */
    MiRespuestaRS actualizarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException;

    /** Elimina una historia médica. */
    MiRespuestaRS eliminarHistoriaMedica(Long historiaMedicaId) throws BadRequestException;
}
