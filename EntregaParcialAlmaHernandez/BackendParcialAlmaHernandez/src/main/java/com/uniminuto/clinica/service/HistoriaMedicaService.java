
package com.uniminuto.clinica.service;

import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.CrearAnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoriaMedicaService {

    RespuestaRs guardarHistoria(HistoriaMedicaRq historiaRq)
            throws BadRequestException;

    RespuestaRs guardarAnotacion(CrearAnotacionHistoriaRq anotacionRq)
            throws BadRequestException;

    List<HistoriaMedicaRs> listarHistorias(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin
    );

    RespuestaRs actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionRq
    ) throws BadRequestException;
}
