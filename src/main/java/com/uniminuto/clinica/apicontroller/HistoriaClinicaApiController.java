package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaClinicaApi;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.HistoriaClinicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador REST de historias clínicas. Delega toda la lógica en {@link HistoriaClinicaService}.
 */
@RestController
public class HistoriaClinicaApiController implements HistoriaClinicaApi {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Page<HistoriaMedica>> listarHistorias(String texto, String estado, Long clienteId,
                                                                Integer mascotaId, Long medicoId,
                                                                LocalDateTime desde, LocalDateTime hasta,
                                                                Pageable pageable) throws BadRequestException {
        return ResponseEntity.ok(historiaClinicaService.listarHistorias(
                texto, estado, clienteId, mascotaId, medicoId, desde, hasta, pageable));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<HistoriaMedica> consultarHistoria(Long id) {
        return ResponseEntity.ok(historiaClinicaService.consultarHistoria(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<MiRespuestaRS> crearHistoria(HistoriaClinicaRq historia) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(historiaClinicaService.crearHistoria(historia));
    }
}