package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un medicamento del inventario de la clínica.
 */
@Entity
@Data
@Table(name = "medicamento")
public class Medicamento {

    /**
     * Identificador único del medicamento.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del medicamento.
     */
    @Column(name = "nombre")
    private String nombre;

    /**
     * Descripción del medicamento.
     */
    @Column(name = "descripcion")
    private String descripcion;

    /**
     * Presentación del medicamento (tabletas, jarabe, inyectable, etc.).
     */
    @Column(name = "presentacion")
    private String presentacion;

    /**
     * Fecha en que se compró el medicamento.
     */
    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    /**
     * Fecha de vencimiento del medicamento.
     */
    @Column(name = "fecha_vence")
    private LocalDate fechaVence;

    /**
     * Fecha y hora en que se creó el registro.
     */
    @Column(name = "fecha_creacion_registro")
    private LocalDateTime fechaCreacionRegistro;

    /**
     * Fecha y hora de la última modificación del registro.
     */
    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;
}
