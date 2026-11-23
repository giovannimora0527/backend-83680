package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Sesion abierta de un usuario (tabla session). El token se envia en cada peticion.
 */
@Entity
@Table(name = "session")
@Data
public class Sesion {

    /** Id de la sesion. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    /** Id del usuario. */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** Token de la sesion. */
    @Column(name = "token", nullable = false, length = 500)
    private String token;

    /** Fecha de inicio. */
    @Column(name = "fecha_ini_sesion")
    private LocalDateTime fechaIniSesion;

    /** Fecha en que vence. */
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
}
