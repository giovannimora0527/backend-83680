package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos para crear o editar un usuario.
 */
@Data
public class UsuarioRq {
    /** Id (solo para editar). */
    private Long id;
    /** Nombre de usuario. */
    private String username;
    /** Contrasena (obligatoria al crear, opcional al editar). */
    private String password;
    /** Id del perfil. */
    private Integer perfilId;
    /** Correo. */
    private String email;
    /** Si esta activo. */
    private Boolean activo;
    /** Medico asociado (solo perfil Medico). */
    private Long medicoId;
}
