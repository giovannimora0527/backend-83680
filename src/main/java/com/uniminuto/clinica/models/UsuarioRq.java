package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class UsuarioRq {
    private String username;
    private String password;
    private String rol;
    private Boolean activo;
    private Long clienteId;
    private Long medicoId;
}
