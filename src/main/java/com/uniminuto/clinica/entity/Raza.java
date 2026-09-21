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
 * Entidad que representa la raza de una mascota.
 */
@Entity
@Table(name = "raza")
@Data
public class Raza {

    /**
     * Identificador único de la raza.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raza_id")
    private Integer razaId;

    /**
     * Nombre de la raza.
     */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Especie a la que pertenece la raza (perro, gato, etc.).
     */
    @Column(name = "especie", nullable = false, length = 100)
    private String especie;

    /**
     * Fecha y hora en que se creó la raza.
     */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de la última modificación de la raza.
     */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
