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

/**
 * Cantidad entregada de un medicamento de la formula desde un lote especifico.
 */
@Entity
@Data
@Table(name = "despacho_detalle")
public class DespachoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "despacho_id", nullable = false)
    private Long despachoId;

    @Column(name = "formula_detalle_id", nullable = false)
    private Long formulaDetalleId;

    /** Lote del que salieron las unidades. */
    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private MedicamentoLote lote;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}
