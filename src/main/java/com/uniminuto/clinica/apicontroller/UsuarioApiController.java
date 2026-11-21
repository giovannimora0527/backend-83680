package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.UsuarioApi;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.service.AutenticacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsuarioApiController implements UsuarioApi {
    private final AutenticacionService autenticacionService;

    public UsuarioApiController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @Override
    public ResponseEntity<List<UsuarioRS>> listarUsuarios() {
        return ResponseEntity.ok(autenticacionService.listarUsuarios());
    }

    @Override
    public ResponseEntity<UsuarioRS> crearUsuario(UsuarioRq solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autenticacionService.crearUsuario(solicitud));
    }

    @Override
    public ResponseEntity<UsuarioRS> actualizarUsuario(Long id, UsuarioRq solicitud) {
        return ResponseEntity.ok(autenticacionService.actualizarUsuario(id, solicitud));
    }
}
