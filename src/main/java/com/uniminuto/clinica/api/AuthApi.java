package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Rutas de inicio y cierre de sesion.
 */
@Tag(name = "Autenticación", description = "Login, sesión actual y logout")
@RequestMapping("/auth")
public interface AuthApi {

    /**
     * Inicia sesion.
     *
     * @param loginRq usuario y contrasena.
     * @return token y menus del perfil.
     */
    @Operation(summary = "Iniciar sesión (no requiere token)")
    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    ResponseEntity<LoginRs> login(@RequestBody LoginRq loginRq);

    /**
     * Datos de la sesion actual.
     *
     * @return usuario y menus.
     */
    @Operation(summary = "Datos del usuario autenticado y sus menús")
    @GetMapping(value = "/me", produces = "application/json")
    ResponseEntity<LoginRs> sesionActual();

    /**
     * Cierra la sesion.
     *
     * @param authorization header con el token.
     * @return respuesta de exito.
     */
    @Operation(summary = "Cerrar sesión")
    @PostMapping(value = "/logout", produces = "application/json")
    ResponseEntity<MiRespuestaRS> logout(@RequestHeader("Authorization") String authorization);
}
