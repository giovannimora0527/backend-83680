package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Pago que hace el cliente a una factura.
 */
@Data
public class PagoRq {
    private Long facturaId;
    private BigDecimal valor;
    /** EFECTIVO, TARJETA o TRANSFERENCIA. */
    private String metodo;
    private String referencia;
}
