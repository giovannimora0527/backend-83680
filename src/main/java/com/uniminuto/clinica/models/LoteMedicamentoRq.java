package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoteMedicamentoRq {
    private String codigoLote;
    private Integer cantidad;
    private BigDecimal precioCompra;
    private LocalDate fechaCompra;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
}
