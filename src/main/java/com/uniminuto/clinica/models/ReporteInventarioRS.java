package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.Medicamento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
/** Respuesta consolidada del reporte de valoración de inventario, alertas y Kardex (Sección 4.2). */
public class ReporteInventarioRS {

    private LocalDateTime fechaGeneracion;
    private String rangoFechas;
    private int diasUmbralProximoVencer = 30;

    // Valoración global de existencias actuales
    private int totalProductos;
    private int totalUnidadesExistencia;
    private BigDecimal valorInventarioCostoAdquisicion = BigDecimal.ZERO;
    private BigDecimal valorPotencialVenta = BigDecimal.ZERO;
    private BigDecimal margenPotencialEstimado = BigDecimal.ZERO;

    // Alertas de inventario
    private int cantidadAgotados;
    private int cantidadStockMinimo;
    private int cantidadProximosVencer;
    private int cantidadVencidos;

    private List<Medicamento> medicamentosAgotados = new ArrayList<>();
    private List<Medicamento> medicamentosStockMinimo = new ArrayList<>();
    private List<Medicamento> medicamentosProximosVencer = new ArrayList<>();
    private List<Medicamento> medicamentosVencidos = new ArrayList<>();

    // Detalle valorizado de todas las existencias
    private List<ItemExistenciaValorizada> existenciasActuales = new ArrayList<>();

    // Kardex de entradas y salidas en el período
    private int totalMovimientosPeriodo;
    private int totalUnidadesEntradaPeriodo;
    private int totalUnidadesSalidaPeriodo;
    private List<ItemMovimientoKardex> movimientosInventario = new ArrayList<>();

    @Data
    public static class ItemExistenciaValorizada {
        private Long medicamentoId;
        private String codigo;
        private String nombre;
        private String presentacion;
        private String lote;
        private String registroInvima;
        private Boolean requiereRegistroInvima;
        private LocalDate fechaCompra;
        private LocalDate fechaIngresoInventario;
        private LocalDate fechaVence;
        private long diasParaVencer;
        private Integer cantidadDisponible;
        private Integer stockMinimo;
        private BigDecimal precioCompra = BigDecimal.ZERO;
        private BigDecimal precioVenta = BigDecimal.ZERO;
        private BigDecimal valorCostoTotal = BigDecimal.ZERO;
        private BigDecimal valorVentaTotal = BigDecimal.ZERO;
        private BigDecimal margenPotencial = BigDecimal.ZERO;
        private String estadoAlerta; // VENCIDO, AGOTADO, STOCK_MINIMO, PROXIMO_VENCER, NORMAL
        private Boolean activo;
    }

    @Data
    public static class ItemMovimientoKardex {
        private Long id;
        private LocalDateTime fechaRegistro;
        private Long medicamentoId;
        private String codigoMedicamento;
        private String nombreMedicamento;
        private String presentacion;
        private String lote;
        private String tipo; // ENTRADA o SALIDA
        private Integer cantidad;
        private String motivo;
        private String referenciaTipo;
        private Long referenciaId;
        private String usuarioRegistro;
    }
}
