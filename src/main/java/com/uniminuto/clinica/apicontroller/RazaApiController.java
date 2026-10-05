package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.RazaApi;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.service.RazaService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RazaApiController implements RazaApi {

    private final RazaService razaService;

    public RazaApiController(RazaService razaService) {
        this.razaService = razaService;
    }

    @Override
    public ResponseEntity<List<Raza>> listarRazas()
            throws BadRequestException {

        return ResponseEntity.ok(
                razaService.listarRazas()
        );
    }

    @Override
    public ResponseEntity<Raza> crearRaza(
            Raza raza)
            throws BadRequestException {

        return ResponseEntity.ok(
                razaService.crearRaza(raza)
        );
    }

    @Override
    public ResponseEntity<Raza> actualizarRaza(
            Integer id,
            Raza raza)
            throws BadRequestException {

        return ResponseEntity.ok(
                razaService.actualizarRaza(id, raza)
        );
    }
}