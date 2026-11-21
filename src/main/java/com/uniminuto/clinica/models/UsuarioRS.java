package com.uniminuto.clinica.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioRS {
    private Long id;
    private String username;
    private String rol;
    private Boolean activo;
    private Long clienteId;
    private Long medicoId;
}
