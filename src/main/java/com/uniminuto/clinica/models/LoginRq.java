package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos para iniciar sesion.
 */
@Data
public class LoginRq {
    /** Nombre de usuario. */
    private String username;
    /** Contrasena. */
    private String password;
}
