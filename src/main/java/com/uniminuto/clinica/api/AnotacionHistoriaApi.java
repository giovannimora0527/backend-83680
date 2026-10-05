package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
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

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(
        origins = "*",
        allowedHeaders = "*"
)
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {

    @GetMapping(
            value = "/listar",
            produces = {"application/json"}
    )
    ResponseEntity<List<AnotacionHistoria>> listarAnotaciones()
            throws BadRequestException;

    @GetMapping(
            value = "/listar-por-rango",
            produces = {"application/json"}
    )
    ResponseEntity<List<AnotacionHistoria>> listarPorRango(
            @RequestParam LocalDateTime fechaInicial,
            @RequestParam LocalDateTime fechaFinal
    ) throws BadRequestException;

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<AnotacionHistoria> crearAnotacion(
            @RequestBody AnotacionHistoria anotacion
    ) throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<AnotacionHistoria> actualizarAnotacion(
            @PathVariable Long id,
            @RequestBody AnotacionHistoria anotacion
    ) throws BadRequestException;
}