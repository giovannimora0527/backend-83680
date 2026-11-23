package com.uniminuto.clinica.model;

import lombok.Data;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
public class MedicamentoRq {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String presentacion;
    private LocalDate fechaCompra;
    private LocalDate fechaVence;
    private String codigo;
    private String tipoProducto;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal cantidadActual;
    private BigDecimal stockMinimo;
    private LocalDate fechaIngreso;
    private LocalDate fechaProximoPedido;
    private String registroInvima;
}
