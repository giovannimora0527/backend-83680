
package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaMedicaApi;
import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.CrearAnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.HistoriaMedicaService;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller que implementa los endpoints de historias médicas.
 */
@RestController
public class HistoriaMedicaApiController implements HistoriaMedicaApi {

    @Autowired
    private HistoriaMedicaService historiaMedicaService;

    /**
     * Crea una nueva historia médica.
     */
    @Override
    public ResponseEntity<RespuestaRs> guardarHistoria(
            @RequestBody HistoriaMedicaRq historiaRq)
            throws BadRequestException {

        return ResponseEntity.ok(
                this.historiaMedicaService.guardarHistoria(historiaRq));
    }

    /**
     * Crea una anotación para una historia médica.
     */
    @Override
    public ResponseEntity<RespuestaRs> guardarAnotacion(
            @RequestBody @Valid CrearAnotacionHistoriaRq anotacionRq)
            throws BadRequestException {

        return ResponseEntity.ok(
                this.historiaMedicaService.guardarAnotacion(anotacionRq));
    }

    /**
     * Lista las historias médicas dentro de un rango de fechas.
     */
    @Override
    public ResponseEntity<List<HistoriaMedicaRs>> listarHistorias(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaIni,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        return ResponseEntity.ok(
                this.historiaMedicaService.listarHistorias(
                        fechaIni, fechaFin));
    }

    /**
     * Actualiza una anotación existente.
     */
    @Override
    public ResponseEntity<RespuestaRs> actualizarAnotacion(
            Long id,
            @RequestBody AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {

        return ResponseEntity.ok(
                this.historiaMedicaService.actualizarAnotacion(
                        id, anotacionRq));
    }
}
