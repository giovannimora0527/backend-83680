package com.uniminuto.clinica.apicontroller;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.service.AnotacionHistoriaService;

/**
 * Controlador REST encargado de gestionar las solicitudes
 * relacionadas con las anotaciones de historias médicas.
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    @Autowired
    private AnotacionHistoriaService anotacionHistoriaService;

    /**
     * Lista las anotaciones dentro de un rango de fechas.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return respuesta HTTP con las anotaciones encontradas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal) throws BadRequestException {

        List<AnotacionHistoria> anotaciones =
                anotacionHistoriaService.obtenerAnotacionesPorFecha(
                        fechaInicial,
                        fechaFinal
                );

        return ResponseEntity.ok(anotaciones);
    }

    /**
     * Crea una nueva anotación de historia.
     *
     * @param anotacionHistoriaRq datos de la anotación.
     * @return respuesta HTTP con la anotación creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public ResponseEntity<AnotacionHistoria> crearAnotacion(
            AnotacionHistoriaRq anotacionHistoriaRq)
            throws BadRequestException {

        AnotacionHistoria anotacion =
                anotacionHistoriaService.crearAnotacion(
                        anotacionHistoriaRq
                );

        return ResponseEntity.ok(anotacion);
    }

    /**
     * Actualiza una anotación de historia existente.
     *
     * @param id identificador de la anotación.
     * @param anotacionHistoriaRq nuevos datos de la anotación.
     * @return respuesta HTTP con la anotación actualizada.
     * @throws BadRequestException si la anotación no existe.
     */
    @Override
    public ResponseEntity<AnotacionHistoria> actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionHistoriaRq)
            throws BadRequestException {

        AnotacionHistoria anotacion =
                anotacionHistoriaService.actualizarAnotacion(
                        id,
                        anotacionHistoriaRq
                );

        return ResponseEntity.ok(anotacion);
    }
}