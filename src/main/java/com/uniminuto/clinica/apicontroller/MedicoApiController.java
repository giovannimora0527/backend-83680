package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicoApi;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los servicios de los medicos.
 */
@RestController
public class MedicoApiController implements MedicoApi {

    /**
     * Servicio con la logica de los medicos.
     */
    @Autowired
    private MedicoService medicoService;

    /**
     * Lista los medicos de la clinica.
     *
     * @return los medicos registrados.
     */
    @Override
    public ResponseEntity<List<Medico>> listarMedicos() {
        return ResponseEntity.ok(this.medicoService.listarMedicos());
    }
}
