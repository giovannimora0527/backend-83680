package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Datos para crear o editar un medicamento/producto. Al crear se puede enviar el primer lote.
 */
@Data
public class MedicamentoRq {
    /** Id (solo editar). */
    private Long id;
    /** Codigo de 8 caracteres alfanumericos. */
    private String codigo;
    /** Nombre. */
    private String nombre;
    /** Descripcion. */
    private String descripcion;
    /** Presentacion. */
    private String presentacion;
    /** MEDICAMENTO o UTENSILIO. */
    private String tipo;
    /** Registro Invima. */
    private String registroInvima;
    /** Precio de venta. */
    private BigDecimal precioVenta;
    /** IVA de venta. */
    private BigDecimal ivaPorcentaje;
    /** Stock minimo. */
    private Integer stockMinimo;
    /** Fecha del proximo pedido. */
    private LocalDate fechaProximoPedido;
    /** Si esta activo (editar). */
    private Boolean activo;
    /** Primer lote (opcional al crear). */
    private LoteRq lote;
}
