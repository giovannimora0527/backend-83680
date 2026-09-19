package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MascotaApi;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.MascotaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/** Controlador que expone los servicios de mascotas. */
public class MascotaApiController implements MascotaApi {

    @Autowired
    /** Servicio con la lógica de mascotas. */
    private MascotaService mascotaService;

    @Override
    /** Delega la consulta de todas las mascotas. */
    public ResponseEntity<List<Mascota>> listarMascotas() throws BadRequestException {
        return ResponseEntity.ok(mascotaService.listarMascotas());
    }

    @Override
    /** Delega la consulta de mascotas por cliente. */
    public ResponseEntity<List<Mascota>> buscarMascotasPorCliente(Long clienteId) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.listarMascotasPorCliente(clienteId));
    }

    @Override
    /** Delega la consulta de mascotas por raza. */
    public ResponseEntity<List<Mascota>> buscarMascotasPorRaza(Integer razaId) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.listarMascotasPorRaza(razaId));
    }

    @Override
    /** Delega el registro de una mascota. */
    public ResponseEntity<MiRespuestaRS> guardarMascota(MascotaRq mascotaRq) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.guardarMascota(mascotaRq));
    }

    @Override
    /** Delega la actualización de una mascota. */
    public ResponseEntity<MiRespuestaRS> actualizarMascota(MascotaRq mascotaRq) throws BadRequestException {
        return ResponseEntity.ok(mascotaService.actualizarMascota(mascotaRq));
    }
}
