package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Medicamento con su informacion de inventario: existencias, precios, vencimientos y alertas.
 */
@Data
public class MedicamentoRs {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String presentacion;
    private String tipo;
    private String registroInvima;
    private BigDecimal precioVenta;
    private BigDecimal ivaPorcentaje;
    private Integer stockMinimo;
    private LocalDate fechaProximoPedido;
    private Boolean activo;
    /** Unidades totales (incluye lotes vencidos). */
    private Integer stockTotal;
    /** Unidades que se pueden dispensar (lotes vigentes). */
    private Integer stockDisponible;
    /** Precio de compra del lote mas reciente. */
    private BigDecimal precioCompra;
    /** Vencimiento mas proximo de los lotes vigentes con existencias. */
    private LocalDate proximoVencimiento;
    /** Alertas: AGOTADO, STOCK_BAJO, POR_VENCER, VENCIDO. */
    private List<String> alertas = new ArrayList<>();
    /** Lotes (solo en el detalle). */
    private List<LoteRs> lotes;
}
