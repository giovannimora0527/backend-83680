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
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lote de un medicamento: cada lote tiene su precio de compra, cantidad y vencimiento.
 */
@Entity
@Data
@Table(name = "medicamento_lote")
public class MedicamentoLote {

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Medicamento del lote. */
    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    /** Numero del lote. */
    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    /** Fecha de compra. */
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    /** Fecha de ingreso al inventario. */
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    /** Fecha de vencimiento (nula para utensilios que no vencen). */
    @Column(name = "fecha_vence")
    private LocalDate fechaVence;

    /** Precio de compra unitario. */
    @Column(name = "precio_compra", nullable = false)
    private BigDecimal precioCompra;

    /** Cantidad que ingreso. */
    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    /** Cantidad que queda. */
    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    /**
     * Indica si el lote ya vencio.
     *
     * @return true si la fecha de vencimiento es anterior a hoy.
     */
    public boolean estaVencido() {
        return fechaVence != null && fechaVence.isBefore(LocalDate.now());
    }
}
