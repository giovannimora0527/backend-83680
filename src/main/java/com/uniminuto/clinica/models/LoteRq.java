package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos de un lote que entra al inventario.
 */
@Data
public class LoteRq {
    /** Medicamento al que entra el lote. */
    private Long medicamentoId;
    /** Numero del lote. */
    private String numeroLote;
    /** Fecha de compra. */
    private LocalDate fechaCompra;
    /** Fecha de vencimiento. */
    private LocalDate fechaVence;
    /** Precio de compra unitario. */
    private BigDecimal precioCompra;
    /** Cantidad. */
    private Integer cantidad;
}
