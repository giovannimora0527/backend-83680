package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Raza;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        origins = "*",
        allowedHeaders = "*"
)
@RequestMapping("/raza")
public interface RazaApi {

    @GetMapping(
            value = "/listar",
            produces = {"application/json"}
    )
    ResponseEntity<List<Raza>> listarRazas()
            throws BadRequestException;

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Raza> crearRaza(
            @RequestBody Raza raza
    ) throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Raza> actualizarRaza(
            @PathVariable Integer id,
            @RequestBody Raza raza
    ) throws BadRequestException;
}