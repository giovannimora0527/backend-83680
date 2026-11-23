package com.uniminuto.clinica.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Datos del usuario que hizo la peticion (se arma a partir del token).
 */
@Data
@AllArgsConstructor
public class SesionUsuario {
    /** Id del usuario. */
    private Long usuarioId;
    /** Nombre de usuario. */
    private String username;
    /** Id del perfil. */
    private Integer perfilId;
    /** Id del medico asociado (solo perfil Medico). */
    private Long medicoId;
}
