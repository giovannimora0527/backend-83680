package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicoApi;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.MedicoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los medicos, solo delega al servicio.
 */
@RestController
public class MedicoApiController implements MedicoApi {

    /** Servicio con la logica de los medicos. */
    @Autowired
    private MedicoService medicoService;

    @Override
    public ResponseEntity<List<Medico>> listarMedicos() {
        return ResponseEntity.ok(this.medicoService.listarMedicos());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarMedico(MedicoRq medicoRq) {
        return ResponseEntity.ok(this.medicoService.guardarMedico(medicoRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarMedico(MedicoRq medicoRq) {
        return ResponseEntity.ok(this.medicoService.actualizarMedico(medicoRq));
    }
}
