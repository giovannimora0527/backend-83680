package com.uniminuto.clinica.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoteMedicamentoRS {
    private Long id;
    private Long medicamentoId;
    private String codigoLote;
    private Integer cantidadDisponible;
    private BigDecimal precioCompra;
    private LocalDate fechaCompra;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private Boolean activo;
}
