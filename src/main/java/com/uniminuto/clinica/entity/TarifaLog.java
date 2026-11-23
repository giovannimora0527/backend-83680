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
 * Historial de cambios de una tarifa (log de tarifas).
 */
@Entity
@Data
@Table(name = "tarifa_log")
public class TarifaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Tarifa modificada. */
    @Column(name = "tarifa_id", nullable = false)
    private Long tarifaId;

    /** CREACION, ACTUALIZACION, ACTIVACION o INACTIVACION. */
    @Column(name = "accion", nullable = false)
    private String accion;

    @Column(name = "valor_anterior")
    private BigDecimal valorAnterior;

    @Column(name = "valor_nuevo")
    private BigDecimal valorNuevo;

    @Column(name = "iva_anterior")
    private BigDecimal ivaAnterior;

    @Column(name = "iva_nuevo")
    private BigDecimal ivaNuevo;

    /** Usuario que hizo el cambio. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    /** Fecha del cambio. */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
