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
public class Especializacion {

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
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "codigo_especializacion", nullable = false, length = 10, unique = true)
    private String codigoEspecializacion;

}
