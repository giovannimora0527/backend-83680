package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.service.EspecializacionService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.uniminuto.clinica.api.EspecializacionApi;

@RestController
public class EspecializacionApiController
        implements EspecializacionApi {

    private final EspecializacionService especializacionService;

    public EspecializacionApiController(
            EspecializacionService especializacionService
    ) {
        this.especializacionService = especializacionService;
    }

    @Override
    public ResponseEntity<List<Especializacion>>
    listarEspecializaciones()
            throws BadRequestException {

        return ResponseEntity.ok(
                especializacionService.listarEspecializaciones()
        );
    }

    @Override
    public ResponseEntity<Especializacion>
    crearEspecializacion(
            Especializacion especializacion
    ) throws BadRequestException {

        return ResponseEntity.ok(
                especializacionService.crearEspecializacion(
                        especializacion
                )
        );
    }

    @Override
    public ResponseEntity<Especializacion>
    actualizarEspecializacion(
            Long id,
            Especializacion especializacion
    ) throws BadRequestException {

        return ResponseEntity.ok(
                especializacionService.actualizarEspecializacion(
                        id,
                        especializacion
                )
        );
    }
}