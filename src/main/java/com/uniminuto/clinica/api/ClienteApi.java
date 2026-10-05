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

import com.uniminuto.clinica.entity.Cliente;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cliente")
public interface ClienteApi {

    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cliente>> listarClientes() throws BadRequestException;

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Cliente> crearCliente(
            @RequestBody Cliente cliente
    ) throws BadRequestException;

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @RequestBody Cliente cliente
    ) throws BadRequestException;

    @PutMapping(
            value = "/estado/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Cliente> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Boolean activo
    ) throws BadRequestException;
}