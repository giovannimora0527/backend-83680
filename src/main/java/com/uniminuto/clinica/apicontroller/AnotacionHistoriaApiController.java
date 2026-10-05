package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.service.AnotacionHistoriaService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;

@RestController
public class AnotacionHistoriaApiController
        implements AnotacionHistoriaApi {

    private final AnotacionHistoriaService anotacionHistoriaService;

    public AnotacionHistoriaApiController(
            AnotacionHistoriaService anotacionHistoriaService
    ) {
        this.anotacionHistoriaService =
                anotacionHistoriaService;
    }

    @Override
    public ResponseEntity<List<AnotacionHistoria>>
    listarAnotaciones()
            throws BadRequestException {

        return ResponseEntity.ok(
                anotacionHistoriaService.listarAnotaciones()
        );
    }

    @Override
    public ResponseEntity<List<AnotacionHistoria>>
    listarPorRango(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    ) throws BadRequestException {

        return ResponseEntity.ok(
                anotacionHistoriaService.listarPorRango(
                        fechaInicial,
                        fechaFinal
                )
        );
    }

    @Override
    public ResponseEntity<AnotacionHistoria>
    crearAnotacion(
            AnotacionHistoria anotacion
    ) throws BadRequestException {

        return ResponseEntity.ok(
                anotacionHistoriaService.crearAnotacion(
                        anotacion
                )
        );
    }

    @Override
    public ResponseEntity<AnotacionHistoria>
    actualizarAnotacion(
            Long id,
            AnotacionHistoria anotacion
    ) throws BadRequestException {

        return ResponseEntity.ok(
                anotacionHistoriaService.actualizarAnotacion(
                        id,
                        anotacion
                )
        );
    }
}