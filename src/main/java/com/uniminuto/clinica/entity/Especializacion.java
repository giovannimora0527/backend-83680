package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "especializacion")
@Data
/** Representa una especialización médica. */
public class Especializacion {

    /** Identificador de la especialización. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "nombre",
            nullable = false,
            length = 100,
            unique = true
    )
    /** Nombre de la especialización. */
    private String nombre;

    @Column(name = "descripcion")
    /** Descripción de la especialización. */
    private String descripcion;

    @Column(name = "codigo_especializacion", nullable = false, length = 10, unique = true)
    /** Código utilizado para identificarla. */
    private String codigoEspecializacion;

}
    /** Nombre de la especialización. */
    /** Descripción de la especialización. */
    /** Código utilizado para identificarla. */
