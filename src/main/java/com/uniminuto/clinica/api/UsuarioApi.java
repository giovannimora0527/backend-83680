package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/usuario")
public interface UsuarioApi {
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<UsuarioRS>> listarUsuarios();

    @PostMapping(value = "/guardar", consumes = "application/json", produces = "application/json")
    ResponseEntity<UsuarioRS> crearUsuario(@RequestBody UsuarioRq solicitud);

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    ResponseEntity<UsuarioRS> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRq solicitud);
}
