package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
/** Controlador que expone los servicios de anotaciones médicas. */
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    @Autowired
    /** Servicio con la lógica de anotaciones. */
    private  AnotacionHistoriaService anotacionHistoriaService;

    @Override
    /** Recibe y delega la creación de una anotación. */
    public ResponseEntity<MiRespuestaRS> crearAnotacion(AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.crearAnotacion(anotacionRq));
    }

    @Override
    /** Delega la consulta de anotaciones por fecha. */
    public ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.listarAnotacionesPorFecha(fechaInicial, fechaFinal));
    }

    @Override
    /** Recibe y delega la actualización de una anotación. */
    public ResponseEntity<MiRespuestaRS> actualizarAnotacion(AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {
        return ResponseEntity.ok(anotacionHistoriaService.actualizarAnotacion(anotacionRq));
    }
}
