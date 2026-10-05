package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.service.HistoriaMedicaService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.uniminuto.clinica.api.HistoriaMedicaApi;

@RestController
public class HistoriaMedicaApiController
        implements HistoriaMedicaApi {

    private final HistoriaMedicaService historiaMedicaService;

    public HistoriaMedicaApiController(
            HistoriaMedicaService historiaMedicaService
    ) {
        this.historiaMedicaService =
                historiaMedicaService;
    }

    @Override
    public ResponseEntity<List<HistoriaMedica>> listarHistorias()
            throws BadRequestException {

        return ResponseEntity.ok(
                historiaMedicaService.listarHistorias()
        );
    }

    @Override
    public ResponseEntity<HistoriaMedica> crearHistoria(
            HistoriaMedica historia
    ) throws BadRequestException {

        return ResponseEntity.ok(
                historiaMedicaService.crearHistoria(
                        historia
                )
        );
    }

    @Override
    public ResponseEntity<HistoriaMedica> actualizarHistoria(
            Long id,
            HistoriaMedica historia
    ) throws BadRequestException {

        return ResponseEntity.ok(
                historiaMedicaService.actualizarHistoria(
                        id,
                        historia
                )
        );
    }
}