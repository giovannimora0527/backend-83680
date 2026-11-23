package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pago registrado a una factura.
 */
@Entity
@Data
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    /** EFECTIVO, TARJETA o TRANSFERENCIA. */
    @Column(name = "metodo", nullable = false)
    private String metodo;

    @Column(name = "referencia")
    private String referencia;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
}
