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

@Entity
@Data
@Table(name = "medicamento")
/** Representa un medicamento disponible en la clínica. */
public class Medicamento {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** Identificador del medicamento. */
    private Long id;

    @Column(name = "nombre")
    /** Nombre del medicamento. */
    private String nombre;

    @Column(name = "descripcion")
    /** Descripción o información adicional. */
    private String descripcion;

    @Column(name = "presentacion")
    /** Presentación comercial del medicamento. */
    private String presentacion;

    @Column(name = "fecha_compra")
    /** Fecha de compra. */
    private LocalDate fechaCompra;

    @Column(name = "fecha_vence")
    /** Fecha de vencimiento. */
    private LocalDate fechaVence;

    @Column(name = "fecha_creacion_registro")
    /** Fecha de creación del registro. */
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_modificacion_registro")
    /** Fecha de la última modificación. */
    private LocalDateTime fechaModificacionRegistro;
}
