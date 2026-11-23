package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lote de un medicamento con su estado (VIGENTE, POR_VENCER, VENCIDO, AGOTADO).
 */
@Data
public class LoteRs {
    private Long id;
    private String numeroLote;
    private LocalDate fechaCompra;
    private LocalDateTime fechaIngreso;
    private LocalDate fechaVence;
    private BigDecimal precioCompra;
    private Integer cantidadInicial;
    private Integer cantidadDisponible;
    private String estado;
}
