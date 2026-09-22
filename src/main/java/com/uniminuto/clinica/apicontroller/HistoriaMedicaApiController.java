package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaMedicaApi;
import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller que implementa los endpoints de historias médicas.
 */
@RestController
public class HistoriaMedicaApiController
        implements HistoriaMedicaApi {

    /**
     * Servicio utilizado para ejecutar las operaciones
     * de historias médicas.
     */
    @Autowired
    private HistoriaMedicaService historiaMedicaService;

    /**
     * Crea una nueva historia médica.
     *
     * @param historiaRq datos necesarios para crear la historia.
     * @return respuesta del servicio.
     * @throws BadRequestException si los datos no son válidos.
     */
    @Override
    public ResponseEntity<RespuestaRs> guardarHistoria(
            @RequestBody HistoriaMedicaRq historiaRq)
            throws BadRequestException {

        return ResponseEntity.ok(
                this.historiaMedicaService
                        .guardarHistoria(historiaRq));
    }

    /**
     * Lista las historias médicas dentro de un rango de fechas.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de historias médicas.
     */
    @Override
    public ResponseEntity<List<HistoriaMedicaRs>> listarHistorias(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin) {

        return ResponseEntity.ok(
                this.historiaMedicaService
                        .listarHistorias(fechaIni, fechaFin));
    }

    /**
     * Actualiza una anotación de una historia médica.
     *
     * @param id identificador de la anotación que se desea actualizar.
     * @param anotacionRq nuevos datos de la anotación.
     * @return respuesta del servicio.
     * @throws BadRequestException si la anotación no existe.
     */
    @Override
    public ResponseEntity<RespuestaRs> actualizarAnotacion(
            Long id,
            @RequestBody AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {

        return ResponseEntity.ok(
                this.historiaMedicaService
                        .actualizarAnotacion(
                                id,
                                anotacionRq));
    }
}