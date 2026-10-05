package com.uniminuto.clinica.api;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uniminuto.clinica.entity.Mascota;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mascota")
public interface MascotaApi {

    @GetMapping(
            value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotas()
            throws BadRequestException;

    @GetMapping(
            value = "/listar-by-cliente",
            produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorCliente(
            @RequestParam Long clienteId)
            throws BadRequestException;

    @GetMapping(
            value = "/listar-by-raza",
            produces = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorRaza(
            @RequestParam Integer razaId)
            throws BadRequestException;

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<Mascota> crearMascota(
            @RequestBody Mascota mascota)
            throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<Mascota> actualizarMascota(
            @PathVariable Integer id,
            @RequestBody Mascota mascota)
            throws BadRequestException;
}