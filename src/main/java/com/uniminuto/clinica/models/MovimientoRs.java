package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Movimiento de inventario para mostrar (historial de entradas y salidas).
 */
@Data
public class MovimientoRs {
    private Long id;
    private LocalDateTime fecha;
    private String tipo;
    private Long medicamentoId;
    private String medicamentoCodigo;
    private String medicamentoNombre;
    private String numeroLote;
    private Integer cantidad;
    private Integer saldoLote;
    private String usuario;
    private Long formulaId;
    private Long despachoId;
    private String observacion;
}
