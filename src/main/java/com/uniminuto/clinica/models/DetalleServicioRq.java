package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
/** Datos de un servicio médico incluido en la factura. */
public class DetalleServicioRq {
    /** Código único del servicio en la atención (ej. CONSULTA_GENERAL) para evitar doble cobro. */
    private String codigoServicio;
    /** Descripción o concepto del servicio prestado. */
    private String concepto;
    /** Cantidad del servicio (por defecto 1). */
    private Integer cantidad;
    /** Precio unitario del servicio. */
    private BigDecimal precioUnitario;
    /** Descuento específico sobre este servicio (opcional). */
    private BigDecimal descuentoItem;
}
