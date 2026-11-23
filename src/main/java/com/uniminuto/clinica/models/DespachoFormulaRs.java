package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Detalle de una formula para prepararla y despacharla.
 */
@Data
public class DespachoFormulaRs {
    private FormulaMedicaRs formula;
    private List<DespachoItemRs> items;
    /** Despachos ya realizados de esta formula. */
    private List<DespachoRs> despachos;
    /** Historial de estados. */
    private List<Log> log;

    /**
     * Cambio de estado del despacho.
     */
    @Data
    public static class Log {
        private String estadoAnterior;
        private String estadoNuevo;
        private String usuario;
        private LocalDateTime fecha;
        private String observacion;
    }
}
