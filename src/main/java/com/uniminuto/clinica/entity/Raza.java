package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "raza")
@Data
public class Raza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raza_id")
    /** Identificador de la raza. */
    private Integer razaId;

    @Column(name = "nombre", nullable = false, length = 100)
    /** Nombre de la raza. */
    private String nombre;

    @Column(name = "especie", nullable = false, length = 100)
    /** Especie a la que pertenece la raza. */
    private String especie;

    @Column(name = "fecha_creacion", nullable = false)
    /** Fecha de creación del registro. */
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    /** Fecha de la última modificación. */
    private LocalDateTime fechaModificacion;
}
