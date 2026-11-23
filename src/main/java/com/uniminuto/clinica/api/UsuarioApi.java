package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Rutas de administracion de usuarios (solo administrador).
 */
@Tag(name = "Usuarios", description = "Usuarios y perfiles (solo Administrador)")
@RequestMapping("/usuario")
public interface UsuarioApi {

    /** @return usuarios. */
    @Operation(summary = "Listar usuarios")
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<UsuarioRs>> listarUsuarios();

    /** @return perfiles. */
    @Operation(summary = "Listar perfiles")
    @GetMapping(value = "/perfiles", produces = "application/json")
    ResponseEntity<List<Perfil>> listarPerfiles();

    /**
     * @param usuarioRq datos.
     * @return respuesta.
     */
    @Operation(summary = "Crear usuario")
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> guardarUsuario(@RequestBody UsuarioRq usuarioRq);

    /**
     * @param usuarioRq datos con id.
     * @return respuesta.
     */
    @Operation(summary = "Editar usuario")
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarUsuario(@RequestBody UsuarioRq usuarioRq);
}
