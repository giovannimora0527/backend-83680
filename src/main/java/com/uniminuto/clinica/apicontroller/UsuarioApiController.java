package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.UsuarioApi;
import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRs;
import com.uniminuto.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de usuarios, solo delega al servicio.
 */
@RestController
public class UsuarioApiController implements UsuarioApi {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public ResponseEntity<List<UsuarioRs>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @Override
    public ResponseEntity<List<Perfil>> listarPerfiles() {
        return ResponseEntity.ok(usuarioService.listarPerfiles());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarUsuario(UsuarioRq usuarioRq) {
        return ResponseEntity.ok(usuarioService.guardarUsuario(usuarioRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarUsuario(UsuarioRq usuarioRq) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(usuarioRq));
    }
}
