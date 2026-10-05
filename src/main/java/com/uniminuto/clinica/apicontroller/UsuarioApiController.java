package com.uniminuto.clinica.apicontroller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uniminuto.clinica.api.UsuarioApi;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.service.UsuarioService;

@RestController
public class UsuarioApiController implements UsuarioApi {

    private final UsuarioService usuarioService;

    public UsuarioApiController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }


    // ==========================================
    // LISTAR USUARIOS
    // ==========================================

    @Override
    public ResponseEntity<List<Usuario>> listarUsuarios()
            throws BadRequestException {

        return ResponseEntity.ok(
                usuarioService.listarUsuarios()
        );
    }


    // ==========================================
    // CREAR USUARIO
    // ==========================================

    @Override
    public ResponseEntity<Usuario> crearUsuario(
            Usuario usuario
    ) throws BadRequestException {

        return ResponseEntity.ok(
                usuarioService.crearUsuario(usuario)
        );
    }


    // ==========================================
    // ACTUALIZAR USUARIO
    // ==========================================

    @Override
    public ResponseEntity<Usuario> actualizarUsuario(
            Long id,
            Usuario usuario
    ) throws BadRequestException {

        return ResponseEntity.ok(
                usuarioService.actualizarUsuario(
                        id,
                        usuario
                )
        );
    }


    // ==========================================
    // CAMBIAR ESTADO
    // ==========================================

    @Override
    public ResponseEntity<Usuario> cambiarEstado(
            Long id,
            Boolean activo
    ) throws BadRequestException {

        return ResponseEntity.ok(
                usuarioService.cambiarEstado(
                        id,
                        activo
                )
        );
    }
}
