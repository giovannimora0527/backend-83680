package com.uniminuto.clinica.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DespachoItemRq {
    private Integer medicamentoId;
    private Long loteId;
    private BigDecimal cantidad;
}
