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
 * Factura de una atencion (cita atendida). Guarda los valores del momento
 * en que se genero, aunque despues cambien las tarifas.
 */
@Entity
@Data
@Table(name = "factura")
public class Factura {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String PAGO_PARCIAL = "PAGO_PARCIAL";
    public static final String PAGADA = "PAGADA";
    public static final String ANULADA = "ANULADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Numero consecutivo (FV-000001). */
    @Column(name = "numero", nullable = false)
    private String numero;

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    /** Nombre de la especialidad al momento de facturar. */
    @Column(name = "especialidad")
    private String especialidad;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "iva", nullable = false)
    private BigDecimal iva;

    @Column(name = "descuento", nullable = false)
    private BigDecimal descuento;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @Column(name = "total_pagado", nullable = false)
    private BigDecimal totalPagado;

    /** PENDIENTE, PAGO_PARCIAL, PAGADA o ANULADA. */
    @Column(name = "estado", nullable = false)
    private String estado;

    /** Usuario que genero la factura. */
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT")
    private String motivoAnulacion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "usuario_anulacion_id")
    private Long usuarioAnulacionId;
}
