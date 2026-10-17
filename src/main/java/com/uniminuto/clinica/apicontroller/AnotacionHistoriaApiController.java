package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AnotacionHistoriaApi;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de anotaciones. Delega toda la lógica en {@link AnotacionHistoriaService}.
 */
@RestController
public class AnotacionHistoriaApiController implements AnotacionHistoriaApi {

    @Autowired
    private AnotacionHistoriaService anotacionHistoriaService;

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<Page<AnotacionHistoria>> listarAnotaciones(Long historiaId, String texto,
                                                                     Pageable pageable) {
        return ResponseEntity.ok(anotacionHistoriaService.listarAnotaciones(historiaId, texto, pageable));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<AnotacionHistoria> consultarAnotacion(Long id) {
        return ResponseEntity.ok(anotacionHistoriaService.consultarAnotacion(id));
    }

    /** {@inheritDoc} */
    @Override
    public ResponseEntity<MiRespuestaRS> crearAnotacionHistoria(AnotacionHistoriaRq anotacionHistoria)
            throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(anotacionHistoriaService.crearAnotacionHistoria(anotacionHistoria));
    }
}