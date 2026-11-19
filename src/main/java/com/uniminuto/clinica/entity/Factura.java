package com.uniminuto.clinica.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura")
@Data
/** Representa una factura generada por una atención médica (cita atendida) y sus medicamentos dispensados. */
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    /** Identificador único de la factura. */
    private Long id;

    @Column(name = "numero_factura", nullable = false, unique = true, length = 30)
    /** Consecutivo o código único de la factura (ej. FAC-2026-0001). */
    private String numeroFactura;

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    /** Cita atendida que origina la factura. */
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    /** Propietario responsable de la factura. */
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    /** Mascota (paciente) atendida. */
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    /** Veterinario que realizó la atención. */
    private Medico medico;

    @Column(name = "fecha_generacion", nullable = false)
    /** Fecha y hora de emisión de la factura. */
    private LocalDateTime fechaGeneracion;

    @Column(name = "subtotal_servicios", nullable = false, precision = 12, scale = 2)
    /** Suma de los ítems de tipo SERVICIO con precio histórico congelado. */
    private BigDecimal subtotalServicios = BigDecimal.ZERO;

    @Column(name = "subtotal_medicamentos", nullable = false, precision = 12, scale = 2)
    /** Suma de los ítems de tipo MEDICAMENTO efectivamente dispensados con precio histórico congelado. */
    private BigDecimal subtotalMedicamentos = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    /** Subtotal bruto antes de descuentos e impuestos. */
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "porcentaje_descuento", nullable = false, precision = 5, scale = 2)
    /** Porcentaje de descuento general aplicado sobre la factura. */
    private BigDecimal porcentajeDescuento = BigDecimal.ZERO;

    @Column(name = "valor_descuento", nullable = false, precision = 12, scale = 2)
    /** Valor monetario total descontado. */
    private BigDecimal valorDescuento = BigDecimal.ZERO;

    @Column(name = "porcentaje_impuesto", nullable = false, precision = 5, scale = 2)
    /** Porcentaje de impuesto aplicado según configuración (ej. 19.00, 5.00, 0.00). */
    private BigDecimal porcentajeImpuesto = new BigDecimal("19.00");

    @Column(name = "valor_impuesto", nullable = false, precision = 12, scale = 2)
    /** Monto de impuesto calculado sobre (subtotal - valorDescuento). */
    private BigDecimal valorImpuesto = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    /** Valor total neto a pagar. */
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "total_pagado", nullable = false, precision = 12, scale = 2)
    /** Suma acumulada de los pagos/abonos registrados en la factura. */
    private BigDecimal totalPagado = BigDecimal.ZERO;

    @Column(name = "saldo_pendiente", nullable = false, precision = 12, scale = 2)
    /** Saldo pendiente por pagar. */
    private BigDecimal saldoPendiente = BigDecimal.ZERO;

    @Column(name = "estado", nullable = false, length = 20)
    /** Estado del pago: PENDIENTE, PARCIAL, PAGADA o ANULADA. */
    private String estado = "PENDIENTE";

    @Column(name = "observaciones", columnDefinition = "TEXT")
    /** Observaciones generales de la factura. */
    private String observaciones;

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT")
    /** Motivo de anulación cuando la factura pasa a estado ANULADA. */
    private String motivoAnulacion;

    @Column(name = "fecha_anulacion")
    /** Fecha y hora en que se anuló la factura. */
    private LocalDateTime fechaAnulacion;

    @Column(name = "historial_operaciones", columnDefinition = "TEXT")
    /** Registro cronológico de trazabilidad de operaciones sobre la factura. */
    private String historialOperaciones;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    /** Líneas de detalle de servicios y medicamentos dispensados. */
    private List<DetalleFactura> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("fechaPago DESC")
    /** Pagos o abonos registrados sobre la factura. */
    private List<PagoFactura> pagos = new ArrayList<>();
}
