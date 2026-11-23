package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Medicamento de una formula con lo prescrito y lo ya entregado.
 */
@Data
public class FormulaDetalleRs {
    private Long id;
    private Long medicamentoId;
    private String medicamentoCodigo;
    private String medicamentoNombre;
    private String presentacion;
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;
    private Integer cantidad;
    private String viaAdministracion;
    private String indicaciones;
    private Integer cantidadEntregada;
    /** Unidades vigentes en inventario en este momento. */
    private Integer stockDisponible;
}
