package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factura con sus lineas, pagos y trazabilidad.
 */
@Data
public class FacturaRs {
    private Long id;
    private String numero;
    private Long citaId;
    private LocalDateTime fechaCita;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDocumento;
    private String clienteTelefono;
    private String clienteDireccion;
    private Integer mascotaId;
    private String mascotaNombre;
    private String mascotaEspecie;
    private Long medicoId;
    private String medicoNombre;
    private String especialidad;
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal descuento;
    private BigDecimal total;
    private BigDecimal totalPagado;
    private BigDecimal saldo;
    private String estado;
    private String usuario;
    private String motivoAnulacion;
    private LocalDateTime fechaAnulacion;
    private List<Linea> lineas = new ArrayList<>();
    private List<PagoLinea> pagos = new ArrayList<>();
    private List<LogLinea> log = new ArrayList<>();

    /** Linea de la factura. */
    @Data
    public static class Linea {
        private String tipo;
        private String descripcion;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal ivaPorcentaje;
        private BigDecimal subtotal;
        private BigDecimal ivaValor;
        private BigDecimal total;
    }

    /** Pago registrado. */
    @Data
    public static class PagoLinea {
        private Long id;
        private BigDecimal valor;
        private String metodo;
        private String referencia;
        private LocalDateTime fecha;
        private String usuario;
    }

    /** Operacion sobre la factura. */
    @Data
    public static class LogLinea {
        private String accion;
        private String detalle;
        private String usuario;
        private LocalDateTime fecha;
    }
}
