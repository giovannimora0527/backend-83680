package com.uniminuto.clinica.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRS {
    private String token;
    private long expiresAt;
    private UsuarioRS usuario;
}
