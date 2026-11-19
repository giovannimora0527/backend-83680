package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
/** Datos recibidos para registrar un abono o pago asociado a una factura. */
public class PagoFacturaRq {
    /** Monto a pagar o abonar. */
    private BigDecimal monto;
    /** Método de pago: EFECTIVO, TARJETA, TRANSFERENCIA u OTRO. */
    private String metodoPago;
    /** Referencia o comprobante del pago. */
    private String referenciaPago;
    /** Usuario responsable que registra el pago. */
    private String usuarioRegistro;
    /** Observación del pago. */
    private String observacion;
}
