package com.uniminuto.clinica.api;


import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mascota")
public interface MascotaApi {

    // Lista todas las mascotas.
    @GetMapping(value = "/listar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotas()
            throws BadRequestException;


    // Busca las mascotas asociadas a un cliente.
    @GetMapping(value = "/listar-by-cliente",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorCliente(
            @RequestParam Long clienteId)
            throws BadRequestException;

    // Busca las mascotas que pertenecen a una raza.
    @GetMapping(value = "/listar-by-raza",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorRaza(
            @RequestParam Integer razaId)
            throws BadRequestException;


    // Registra una nueva mascota.
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarMascota(
            @RequestBody MascotaRq mascotaRq)
            throws BadRequestException;

    // Actualiza los datos de una mascota existente.
    @PostMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMascota(
            @RequestBody MascotaRq mascotaRq)
            throws BadRequestException;
}
