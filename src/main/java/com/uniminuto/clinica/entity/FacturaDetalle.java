package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Linea de una factura (servicio o medicamento) con sus valores historicos.
 */
@Entity
@Data
@Table(name = "factura_detalle")
public class FacturaDetalle {

    public static final String SERVICIO = "SERVICIO";
    public static final String MEDICAMENTO = "MEDICAMENTO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    /** SERVICIO o MEDICAMENTO. */
    @Column(name = "tipo", nullable = false)
    private String tipo;

    /** Id de la tarifa o del medicamento. */
    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "iva_porcentaje", nullable = false)
    private BigDecimal ivaPorcentaje;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    @Column(name = "iva_valor", nullable = false)
    private BigDecimal ivaValor;

    @Column(name = "total", nullable = false)
    private BigDecimal total;
}
