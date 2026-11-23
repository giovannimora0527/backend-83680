package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Usuario para mostrar en el front (sin contrasena).
 */
@Data
public class UsuarioRs {
    /** Id. */
    private Long id;
    /** Nombre de usuario. */
    private String username;
    /** Correo. */
    private String email;
    /** Id del perfil. */
    private Integer perfilId;
    /** Nombre del perfil. */
    private String perfilNombre;
    /** Si esta activo. */
    private Boolean activo;
    /** Fecha de creacion. */
    private LocalDateTime fechaCreacion;
    /** Medico asociado. */
    private Long medicoId;
    /** Nombre del medico asociado. */
    private String medicoNombre;
}
