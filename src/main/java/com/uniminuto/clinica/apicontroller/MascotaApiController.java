package com.uniminuto.clinica.apicontroller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uniminuto.clinica.api.MascotaApi;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.service.MascotaService;

@RestController
public class MascotaApiController implements MascotaApi {

    private final MascotaService mascotaService;

    public MascotaApiController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @Override
    public ResponseEntity<List<Mascota>> listarMascotas()
            throws BadRequestException {

        return ResponseEntity.ok(
                mascotaService.listarMascotas());
    }

    @Override
    public ResponseEntity<List<Mascota>> buscarMascotasPorCliente(
            Long clienteId)
            throws BadRequestException {

        return ResponseEntity.ok(
                mascotaService.listarMascotasPorCliente(clienteId));
    }

    @Override
    public ResponseEntity<List<Mascota>> buscarMascotasPorRaza(
            Integer razaId)
            throws BadRequestException {

        return ResponseEntity.ok(
                mascotaService.listarMascotasPorRaza(razaId));
    }

    @Override
    public ResponseEntity<Mascota> crearMascota(
            Mascota mascota)
            throws BadRequestException {

        return ResponseEntity.ok(
                mascotaService.crearMascota(mascota));
    }

    @Override
    public ResponseEntity<Mascota> actualizarMascota(
            Integer id,
            Mascota mascota)
            throws BadRequestException {

        return ResponseEntity.ok(
                mascotaService.actualizarMascota(id, mascota));
    }
}