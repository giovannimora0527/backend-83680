package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos para crear o editar una tarifa.
 */
@Data
public class TarifaRq {
    private Long id;
    private String codigo;
    private String nombre;
    /** CONSULTA, PROCEDIMIENTO o SERVICIO. */
    private String tipo;
    /** Especialidad (opcional). */
    private Long especializacionId;
    private BigDecimal valor;
    private BigDecimal ivaPorcentaje;
}
