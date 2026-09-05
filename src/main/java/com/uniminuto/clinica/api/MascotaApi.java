package com.uniminuto.clinica.api;


import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mascota")
public interface MascotaApi {

    @GetMapping(value = "/listar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotas()
            throws BadRequestException;


    @GetMapping(value = "/listar-by-cliente",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorCliente(
            @RequestParam Long clienteId)
            throws BadRequestException;

    @GetMapping(value = "/listar-by-raza",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorRaza(
            @RequestParam Integer razaId)
            throws BadRequestException;
}
