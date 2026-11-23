package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro del historial de cambios de una tarifa.
 */
@Data
public class TarifaLogRs {
    private Long id;
    private String accion;
    private BigDecimal valorAnterior;
    private BigDecimal valorNuevo;
    private BigDecimal ivaAnterior;
    private BigDecimal ivaNuevo;
    private String usuario;
    private LocalDateTime fecha;
}
