package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MascotaApi;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los servicios de las mascotas.
 */
@RestController
public class MascotaApiController implements MascotaApi {

    /**
     * Servicio con la logica de las mascotas.
     */
    @Autowired
    private MascotaService mascotaService;

    /**
     * Lista todas las mascotas.
     *
     * @return las mascotas registradas.
     */
    @Override
    public ResponseEntity<List<Mascota>> listarMascotas() {
        return ResponseEntity.ok(mascotaService.listarMascotas());
    }

    /**
     * Lista las mascotas de un cliente.
     *
     * @param clienteId identificador del cliente.
     * @return las mascotas del cliente.
     */
    @Override
    public ResponseEntity<List<Mascota>> buscarMascotasPorCliente(Long clienteId) {
        return ResponseEntity.ok(mascotaService.listarMascotasPorCliente(clienteId));
    }

    /**
     * Lista las mascotas de una raza.
     *
     * @param razaId identificador de la raza.
     * @return las mascotas de la raza.
     */
    @Override
    public ResponseEntity<List<Mascota>> buscarMascotasPorRaza(Integer razaId) {
        return ResponseEntity.ok(mascotaService.listarMascotasPorRaza(razaId));
    }

    /**
     * Guarda una mascota nueva.
     *
     * @param mascotaRq datos de la mascota.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> guardarMascota(MascotaRq mascotaRq) {
        return ResponseEntity.ok(mascotaService.guardarMascota(mascotaRq));
    }

    /**
     * Actualiza una mascota que ya existe.
     *
     * @param mascotaRq datos de la mascota.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> actualizarMascota(MascotaRq mascotaRq) {
        return ResponseEntity.ok(mascotaService.actualizarMascota(mascotaRq));
    }
}
