package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos para generar la factura de una cita atendida.
 */
@Data
public class GenerarFacturaRq {
    /** Cita atendida. */
    private Long citaId;
    /** Descuento en pesos (solo lo puede autorizar el Administrador). */
    private BigDecimal descuento;
}
