package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AuthApi;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de autenticacion, solo delega al servicio.
 */
@RestController
public class AuthApiController implements AuthApi {

    @Autowired
    private AuthService authService;

    @Override
    public ResponseEntity<LoginRs> login(LoginRq loginRq) {
        return ResponseEntity.ok(authService.login(loginRq));
    }

    @Override
    public ResponseEntity<LoginRs> sesionActual() {
        return ResponseEntity.ok(authService.sesionActual());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> logout(String authorization) {
        return ResponseEntity.ok(authService.logout(authorization.replace("Bearer ", "").trim()));
    }
}
