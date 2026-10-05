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

import com.uniminuto.clinica.entity.Usuario;

@CrossOrigin(
        origins = "*",
        allowedHeaders = "*"
)
@RequestMapping("/usuario")
public interface UsuarioApi {

    // ==========================================
    // LISTAR USUARIOS
    // ==========================================

    @GetMapping(
            value = "/listar",
            produces = {"application/json"}
    )
    ResponseEntity<List<Usuario>> listarUsuarios()
            throws BadRequestException;


    // ==========================================
    // CREAR USUARIO
    // ==========================================

    @PostMapping(
            value = "/crear",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Usuario> crearUsuario(
            @RequestBody Usuario usuario
    ) throws BadRequestException;


    // ==========================================
    // ACTUALIZAR USUARIO
    // ==========================================

    @PutMapping(
            value = "/actualizar/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuario
    ) throws BadRequestException;


    // ==========================================
    // CAMBIAR ESTADO
    // ==========================================

    @PutMapping(
            value = "/estado/{id}",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    ResponseEntity<Usuario> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Boolean activo
    ) throws BadRequestException;
}