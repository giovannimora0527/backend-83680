package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.service.MedicoService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.uniminuto.clinica.api.MedicoApi;

@RestController
public class MedicoApiController
        implements MedicoApi{

    private final MedicoService medicoService;

    public MedicoApiController(
            MedicoService medicoService
    ) {
        this.medicoService = medicoService;
    }

    @Override
    public ResponseEntity<List<Medico>> listarMedicos()
            throws BadRequestException {

        return ResponseEntity.ok(
                medicoService.listarMedicos()
        );
    }

    @Override
    public ResponseEntity<Medico> crearMedico(
            Medico medico
    ) throws BadRequestException {

        return ResponseEntity.ok(
                medicoService.crearMedico(medico)
        );
    }

    @Override
    public ResponseEntity<Medico> actualizarMedico(
            Long id,
            Medico medico
    ) throws BadRequestException {

        return ResponseEntity.ok(
                medicoService.actualizarMedico(
                        id,
                        medico
                )
        );
    }
}