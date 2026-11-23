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
 * Entrega (dispensacion) de medicamentos de una formula. No se borra: si hay un
 * error se revierte con un procedimiento autorizado.
 */
@Entity
@Data
@Table(name = "despacho")
public class Despacho {

    /** Despacho confirmado (descontó inventario). */
    public static final String CONFIRMADO = "CONFIRMADO";
    /** Despacho revertido (las unidades volvieron al inventario). */
    public static final String REVERTIDO = "REVERTIDO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "formula_id", nullable = false)
    private Long formulaId;

    @Column(name = "estado", nullable = false)
    private String estado;

    /** Usuario de despacho que entregó. */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "usuario_reversion_id")
    private Long usuarioReversionId;

    @Column(name = "fecha_reversion")
    private LocalDateTime fechaReversion;

    @Column(name = "motivo_reversion", columnDefinition = "TEXT")
    private String motivoReversion;
}
