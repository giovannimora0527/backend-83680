package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Especializacion;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@CrossOrigin(
        origins = "*",
        allowedHeaders = "*"
)
@RequestMapping("/especializacion")
public interface EspecializacionApi {

    @GetMapping(
            value = "/listar",
            produces = {"application/json"}
    )
    ResponseEntity<List<Especializacion>> listarEspecializaciones()
            throws BadRequestException;

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Especializacion> crearEspecializacion(
            @RequestBody Especializacion especializacion
    ) throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Especializacion> actualizarEspecializacion(
            @PathVariable Long id,
            @RequestBody Especializacion especializacion
    ) throws BadRequestException;
}