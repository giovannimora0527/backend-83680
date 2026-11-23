package com.uniminuto.clinica.model;

import lombok.Data;

/**
 *
 * @author lmora
 */
@Data
public class UsuarioRq {
    private String username;
    private String pass;
    private String rol;
}
