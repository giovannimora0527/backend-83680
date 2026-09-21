package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de los servicios de las anotaciones de historia medica.
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    /**
     * Servicio con la logica de las anotaciones de historia medica.
     */
    @Autowired
    private AnotacionHistoriaService anotacionHistoriaService;

    /**
     * Lista las anotaciones entre dos fechas.
     *
     * @param fechaInicial primer dia del rango.
     * @param fechaFinal   ultimo dia del rango.
     * @return las anotaciones del rango.
     */
    @Override
    public ResponseEntity<List<AnotacionHistoriaRs>> listarAnotaciones(LocalDate fechaInicial, LocalDate fechaFinal) {
        return ResponseEntity.ok(anotacionHistoriaService.listarAnotaciones(fechaInicial, fechaFinal));
    }

    /**
     * Guarda una anotacion nueva.
     *
     * @param anotacionRq datos de la anotacion.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> guardarAnotacion(AnotacionHistoriaRq anotacionRq) {
        return ResponseEntity.ok(anotacionHistoriaService.guardarAnotacion(anotacionRq));
    }

    /**
     * Actualiza una anotacion que ya existe.
     *
     * @param anotacionRq datos de la anotacion.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarAnotacion(AnotacionHistoriaRq anotacionRq) {
        return ResponseEntity.ok(anotacionHistoriaService.actualizarAnotacion(anotacionRq));
    }
}
