package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos para confirmar la entrega de medicamentos de una formula.
 */
@Data
public class ConfirmarDespachoRq {
    /** Formula que se despacha. */
    private Long formulaId;
    /** Observacion del despacho. */
    private String observacion;
    /** Medicamentos a entregar. */
    private List<Item> items = new ArrayList<>();

    /**
     * Medicamento de la formula y cantidad a entregar.
     */
    @Data
    public static class Item {
        /** Detalle de la formula. */
        private Long formulaDetalleId;
        /** Cantidad a entregar ahora. */
        private Integer cantidad;
        /** Lotes elegidos (si va vacio se usan los que vencen primero). */
        private List<LoteCantidad> lotes = new ArrayList<>();
    }

    /**
     * Cantidad tomada de un lote.
     */
    @Data
    public static class LoteCantidad {
        private Long loteId;
        private Integer cantidad;
    }
}
