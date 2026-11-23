package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.Menu;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta del login: token, datos del usuario y menus de su perfil.
 */
@Data
public class LoginRs {
    /** Token para enviar en el header Authorization. */
    private String token;
    /** Fecha en que vence la sesion. */
    private LocalDateTime fechaExpiracion;
    /** Id del usuario. */
    private Long usuarioId;
    /** Nombre de usuario. */
    private String username;
    /** Id del perfil. */
    private Integer perfilId;
    /** Nombre del perfil. */
    private String perfilNombre;
    /** Id del medico (si el usuario es medico). */
    private Long medicoId;
    /** Nombre del medico (si el usuario es medico). */
    private String medicoNombre;
    /** Menus que puede ver. */
    private List<Menu> menus;
}
