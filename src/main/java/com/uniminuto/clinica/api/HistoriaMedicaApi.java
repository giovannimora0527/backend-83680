package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HistoriaMedica;
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
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    @GetMapping(
            value = "/listar",
            produces = {"application/json"}
    )
    ResponseEntity<List<HistoriaMedica>> listarHistorias()
            throws BadRequestException;

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<HistoriaMedica> crearHistoria(
            @RequestBody HistoriaMedica historia
    ) throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<HistoriaMedica> actualizarHistoria(
            @PathVariable Long id,
            @RequestBody HistoriaMedica historia
    ) throws BadRequestException;
}