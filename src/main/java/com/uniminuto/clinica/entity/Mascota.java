package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mascota")
@Data
/** Representa una mascota registrada en la clínica. */
public class Mascota {

    /** Identificador de la mascota. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mascota_id")
    private Integer mascotaId;

    @Column(name = "nombre_mascota", nullable = false, length = 100)
    /** Nombre de la mascota. */
    private String nombreMascota;

    @Column(name = "edad", nullable = false)
    /** Edad de la mascota. */
    private Integer edad;

    @ManyToOne
    @JoinColumn(name = "raza_id", nullable = false)
    /** Raza a la que pertenece la mascota. */
    private Raza raza;

    @Column(name = "fecha_registro", nullable = false)
    /** Fecha en que se registró la mascota. */
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_modificacion")
    /** Fecha de la última modificación. */
    private LocalDateTime fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    /** Cliente propietario de la mascota. */
    private Cliente cliente;
}
    /** Nombre de la mascota. */
    /** Edad de la mascota. */
    /** Raza a la que pertenece la mascota. */
    /** Fecha en que se registró la mascota. */
    /** Fecha de la última modificación. */
    /** Cliente propietario de la mascota. */
