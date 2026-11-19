package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
/** Respuesta consolidada del reporte financiero y contable (Sección 4.1). */
public class ReporteFinancieroRS {

    private LocalDateTime fechaGeneracion;
    private String rangoFechas;

    // Indicadores monetarios del período (sobre facturas activas no anuladas)
    private BigDecimal ingresosTotalesFacturados = BigDecimal.ZERO;
    private BigDecimal ingresosRecaudados = BigDecimal.ZERO;
    private BigDecimal saldoPendienteTotal = BigDecimal.ZERO;
    private BigDecimal ingresosConsultasServicios = BigDecimal.ZERO;
    private BigDecimal ingresosVentaMedicamentos = BigDecimal.ZERO;
    private BigDecimal impuestosGenerados = BigDecimal.ZERO;
    private BigDecimal impuestosRecaudados = BigDecimal.ZERO;
    private BigDecimal descuentosAplicados = BigDecimal.ZERO;

    // Conteo y montos por estado de factura
    private int totalFacturasGeneradas;
    private int facturasPagadas;
    private BigDecimal montoFacturasPagadas = BigDecimal.ZERO;
    private int facturasPendientes;
    private BigDecimal montoFacturasPendientes = BigDecimal.ZERO;
    private int pagosParciales;
    private BigDecimal montoPagosParciales = BigDecimal.ZERO;
    private int facturasAnuladas;
    private BigDecimal montoFacturasAnuladas = BigDecimal.ZERO;

    // Desgloses agrupados
    private List<ItemFinancieroAgrupado> ingresosPorEspecialidad = new ArrayList<>();
    private List<ItemFinancieroAgrupado> ingresosPorVeterinario = new ArrayList<>();
    private List<ResumenPeriodoFinanciero> resumenPorDia = new ArrayList<>();
    private List<ResumenPeriodoFinanciero> resumenPorMes = new ArrayList<>();
    private List<ResumenPeriodoFinanciero> resumenPorAnio = new ArrayList<>();

    @Data
    public static class ItemFinancieroAgrupado {
        private Long id;
        private String categoria;
        private String subcategoria;
        private int cantidadFacturas;
        private BigDecimal ingresosServicios = BigDecimal.ZERO;
        private BigDecimal ingresosMedicamentos = BigDecimal.ZERO;
        private BigDecimal descuentos = BigDecimal.ZERO;
        private BigDecimal impuestos = BigDecimal.ZERO;
        private BigDecimal totalFacturado = BigDecimal.ZERO;
        private BigDecimal totalRecaudado = BigDecimal.ZERO;
    }

    @Data
    public static class ResumenPeriodoFinanciero {
        private String periodo;
        private int cantidadFacturas;
        private BigDecimal ingresosServicios = BigDecimal.ZERO;
        private BigDecimal ingresosMedicamentos = BigDecimal.ZERO;
        private BigDecimal descuentos = BigDecimal.ZERO;
        private BigDecimal impuestos = BigDecimal.ZERO;
        private BigDecimal totalFacturado = BigDecimal.ZERO;
        private BigDecimal totalRecaudado = BigDecimal.ZERO;
    }
}
