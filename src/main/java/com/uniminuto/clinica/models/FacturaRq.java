package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
/** Datos recibidos para generar una factura sobre una cita atendida. */
public class FacturaRq {
    /** Identificador de la cita atendida que origina la factura. */
    private Long citaId;
    /** Servicios médicos prestados durante la atención (al menos la consulta principal). */
    private List<DetalleServicioRq> servicios;
    /** Porcentaje de descuento general (0 a 100). */
    private BigDecimal porcentajeDescuento;
    /** Valor de descuento general fijo (opcional si no se usa porcentaje). */
    private BigDecimal valorDescuento;
    /** Porcentaje de impuesto aplicable según configuración (ej. 19.00, 5.00, 0.00). */
    private BigDecimal porcentajeImpuesto;
    /** Observaciones generales de la factura. */
    private String observaciones;
    /** Monto de pago o abono inicial al emitir la factura (opcional). */
    private BigDecimal montoPagoInicial;
    /** Método del pago inicial (EFECTIVO, TARJETA, TRANSFERENCIA, OTRO). */
    private String metodoPagoInicial;
    /** Referencia del pago inicial. */
    private String referenciaPagoInicial;
    /** Usuario o perfil responsable que genera la factura. */
    private String usuarioRegistro;
}
