package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_medicamento")
@Data
/** Registra las entradas y salidas del inventario por medicamento. */
public class MovimientoMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "medicamento_id", nullable = false)
    /** Identificador del medicamento afectado. */
    private Long medicamentoId;

    @Column(name = "tipo", nullable = false, length = 20)
    /** Tipo de movimiento: ENTRADA o SALIDA. */
    private String tipo;

    @Column(name = "cantidad", nullable = false)
    /** Cantidad del movimiento. */
    private Integer cantidad;

    @Column(name = "motivo", length = 200)
    /** Motivo del movimiento. */
    private String motivo;

    @Column(name = "referencia_tipo", length = 50)
    /** Origen del movimiento: fórmula, compra, ajuste. */
    private String referenciaTipo;

    @Column(name = "referencia_id")
    /** Identificador de la referencia asociada. */
    private Long referenciaId;

    @Column(name = "fecha_registro", nullable = false)
    /** Fecha en que se registró el movimiento. */
    private LocalDateTime fechaRegistro;

    @Column(name = "observacion", columnDefinition = "TEXT")
    /** Descripción adicional del movimiento. */
    private String observacion;
}
