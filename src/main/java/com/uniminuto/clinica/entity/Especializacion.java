package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa la especialización de un médico veterinario.
 */
@Entity
@Table(name = "especializacion")
@Data
public class Especializacion {

    /**
     * Identificador único de la especialización.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nombre de la especialización (único).
     */
    @Column(
            name = "nombre",
            nullable = false,
            length = 100,
            unique = true
    )
    private String nombre;

    /**
     * Descripción de la especialización.
     */
    @Column(name = "descripcion")
    private String descripcion;

    /**
     * Código corto de la especialización (único).
     */
    @Column(name = "codigo_especializacion", nullable = false, length = 10, unique = true)
    private String codigoEspecializacion;

}
