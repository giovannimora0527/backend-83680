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
 * Perfil (rol) del usuario.
 */
@Entity
@Table(name = "perfil")
@Data
public class Perfil {

    /** Perfil administrador. */
    public static final int ADMINISTRADOR = 1;
    /** Perfil recepcion / secretaria. */
    public static final int RECEPCION = 2;
    /** Perfil medico veterinario. */
    public static final int MEDICO = 3;
    /** Perfil de despacho de medicamentos. */
    public static final int DESPACHO_MEDICO = 4;

    /** Id del perfil. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perfil_id")
    private Integer perfilId;

    /** Nombre del perfil. */
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /** Si esta activo. */
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    /** Fecha de creacion. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha de modificacion. */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
