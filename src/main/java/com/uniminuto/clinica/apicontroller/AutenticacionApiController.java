package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AutenticacionApi;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.service.AutenticacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacionApiController implements AutenticacionApi {
    private final AutenticacionService autenticacionService;

    public AutenticacionApiController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @Override
    public ResponseEntity<LoginRS> iniciarSesion(LoginRq solicitud) {
        return ResponseEntity.ok(autenticacionService.iniciarSesion(solicitud));
    }
}
