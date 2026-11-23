package com.uniminuto.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Usuario del sistema. El campo rol guarda el id del perfil.
 */
@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    /** Id del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Nombre de usuario para ingresar. */
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    /** Contrasena guardada en MD5 (igual al script del docente). No se envia al front. */
    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    /** Id del perfil (1 Administrador, 2 Recepcion, 3 Medico, 4 DESPACHO_MEDICO). */
    @Column(name = "rol", nullable = false)
    private Integer rol;

    /** Fecha de creacion. */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /** Si puede ingresar. */
    @Column(name = "activo")
    private Boolean activo;

    /** Correo. */
    @Column(name = "email", nullable = false)
    private String email;
}
