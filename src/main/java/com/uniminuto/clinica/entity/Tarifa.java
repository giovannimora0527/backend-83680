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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tarifa de un servicio (consulta, procedimiento o servicio). El administrador
 * cambia los valores sin tocar el codigo.
 */
@Entity
@Data
@Table(name = "tarifa")
public class Tarifa {

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Codigo unico del servicio. */
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    /** Nombre del servicio. */
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /** CONSULTA, PROCEDIMIENTO o SERVICIO. */
    @Column(name = "tipo", nullable = false)
    private String tipo;

    /** Especialidad a la que aplica (null = general). */
    @ManyToOne
    @JoinColumn(name = "especializacion_id")
    private Especializacion especializacion;

    /** Valor antes de IVA. */
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    /** Porcentaje de IVA. */
    @Column(name = "iva_porcentaje", nullable = false)
    private BigDecimal ivaPorcentaje;

    /** Si esta activa. */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /** Fecha de creacion. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha de modificacion. */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
