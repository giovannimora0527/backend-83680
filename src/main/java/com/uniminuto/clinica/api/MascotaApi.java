package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de los servicios de las mascotas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mascota")
public interface MascotaApi {

    /**
     * Lista todas las mascotas.
     *
     * @return las mascotas registradas.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotas();

    /**
     * Lista las mascotas de un cliente.
     *
     * @param clienteId identificador del cliente.
     * @return las mascotas del cliente.
     */
    @GetMapping(value = "/listar-by-cliente",
            produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorCliente(
            @RequestParam Long clienteId);

    /**
     * Lista las mascotas de una raza.
     *
     * @param razaId identificador de la raza.
     * @return las mascotas de la raza.
     */
    @GetMapping(value = "/listar-by-raza",
            produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorRaza(
            @RequestParam Integer razaId);

    /**
     * Guarda una mascota nueva.
     *
     * @param mascotaRq datos de la mascota.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarMascota(
            @RequestBody MascotaRq mascotaRq);

    /**
     * Actualiza una mascota que ya existe.
     *
     * @param mascotaRq datos de la mascota, incluido su id.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMascota(
            @RequestBody MascotaRq mascotaRq);
}
