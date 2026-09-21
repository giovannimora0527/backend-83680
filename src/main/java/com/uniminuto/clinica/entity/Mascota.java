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

/**
 * Entidad que representa una mascota registrada en la clínica.
 */
@Entity
@Table(name = "mascota")
@Data
public class Mascota {

    /**
     * Identificador único de la mascota.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mascota_id")
    private Integer mascotaId;

    /**
     * Nombre de la mascota.
     */
    @Column(name = "nombre_mascota", nullable = false, length = 100)
    private String nombreMascota;

    /**
     * Edad de la mascota en años.
     */
    @Column(name = "edad", nullable = false)
    private Integer edad;

    /**
     * Raza de la mascota.
     */
    @ManyToOne
    @JoinColumn(name = "raza_id", nullable = false)
    private Raza raza;

    /**
     * Fecha y hora en que se registró la mascota.
     */
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Fecha y hora de la última modificación de la mascota.
     */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /**
     * Cliente propietario de la mascota.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
