package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.HistoriaMedicaApi;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/** Controlador que expone los servicios de historias médicas. */
public class HistoriaMedicaApiController implements HistoriaMedicaApi {

    @Autowired
    /** Servicio con la lógica de historias médicas. */
    private HistoriaMedicaService historiaMedicaService;

    @Override
    /** Delega la consulta de historias médicas. */
    public ResponseEntity<List<HistoriaMedica>> listarHistoriasMedicas() throws BadRequestException {
        return ResponseEntity.ok(historiaMedicaService.listarHistoriasMedicas());
    }

    @Override
    /** Delega la creación de una historia médica. */
    public ResponseEntity<MiRespuestaRS> crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException {
        return ResponseEntity.ok(historiaMedicaService.crearHistoriaMedica(historiaMedicaRq));
    }

    @Override
    /** Delega la actualización de una historia médica. */
    public ResponseEntity<MiRespuestaRS> actualizarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException {
        return ResponseEntity.ok(historiaMedicaService.actualizarHistoriaMedica(historiaMedicaRq));
    }

    @Override
    /** Delega la eliminación de una historia médica. */
    public ResponseEntity<MiRespuestaRS> eliminarHistoriaMedica(Long historiaMedicaId)
            throws BadRequestException {
        return ResponseEntity.ok(historiaMedicaService.eliminarHistoriaMedica(historiaMedicaId));
    }
}
