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
 * Movimiento de inventario (entrada, carga masiva, salida por despacho o reversion).
 */
@Entity
@Data
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    /** Entrada manual de un lote. */
    public static final String ENTRADA = "ENTRADA";
    /** Entrada por carga masiva. */
    public static final String CARGA_MASIVA = "CARGA_MASIVA";
    /** Salida por dispensacion. */
    public static final String SALIDA = "SALIDA";
    /** Devolucion por reversion de un despacho. */
    public static final String REVERSION = "REVERSION";

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Medicamento. */
    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    /** Lote afectado. */
    @Column(name = "lote_id")
    private Long loteId;

    /** Tipo de movimiento. */
    @Column(name = "tipo", nullable = false)
    private String tipo;

    /** Cantidad (siempre positiva). */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /** Cantidad que quedo en el lote despues del movimiento. */
    @Column(name = "saldo_lote", nullable = false)
    private Integer saldoLote;

    /** Fecha y hora. */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    /** Usuario responsable. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    /** Formula relacionada (salidas). */
    @Column(name = "formula_id")
    private Long formulaId;

    /** Despacho relacionado (salidas y reversiones). */
    @Column(name = "despacho_id")
    private Long despachoId;

    /** Observacion. */
    @Column(name = "observacion")
    private String observacion;
}
