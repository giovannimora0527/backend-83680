package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Despacho realizado (historial / log de despacho de formulas).
 */
@Data
public class DespachoRs {
    private Long id;
    private Long formulaId;
    private LocalDateTime fecha;
    private String usuario;
    private String estado;
    private String observacion;
    private String mascota;
    private String propietario;
    private String medico;
    private String estadoFormula;
    private String usuarioReversion;
    private LocalDateTime fechaReversion;
    private String motivoReversion;
    private List<Linea> lineas = new ArrayList<>();

    /**
     * Medicamento entregado desde un lote.
     */
    @Data
    public static class Linea {
        private String medicamento;
        private String numeroLote;
        private LocalDate fechaVence;
        private Integer cantidad;
    }
}
