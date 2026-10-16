package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaMedicaApi;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de las historias clinicas, solo delega al servicio.
 */
@RestController
public class HistoriaMedicaApiController implements HistoriaMedicaApi {

    /** Servicio con la logica de las historias. */
    @Autowired
    private HistoriaMedicaService historiaMedicaService;

    @Override
    public ResponseEntity<List<HistoriaMedicaRs>> listarHistorias() {
        return ResponseEntity.ok(historiaMedicaService.listarHistorias());
    }

    @Override
    public ResponseEntity<HistoriaMedicaRs> consultarHistoria(Long historiaId) {
        return ResponseEntity.ok(historiaMedicaService.consultarHistoria(historiaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarHistoria(HistoriaMedicaRq historiaRq) {
        return ResponseEntity.ok(historiaMedicaService.guardarHistoria(historiaRq));
    }
}
