package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface AutenticacionApi {
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    ResponseEntity<LoginRS> iniciarSesion(@RequestBody LoginRq solicitud);
}
