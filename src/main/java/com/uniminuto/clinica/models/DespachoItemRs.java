package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Un medicamento de la formula visto desde el despacho: prescrito, entregado,
 * pendiente, existencias y lotes.
 */
@Data
public class DespachoItemRs {
    private Long formulaDetalleId;
    private Long medicamentoId;
    private String medicamentoCodigo;
    private String medicamentoNombre;
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;
    private String viaAdministracion;
    private String indicaciones;
    private Integer cantidadPrescrita;
    private Integer cantidadEntregada;
    private Integer cantidadPendiente;
    /** Unidades vigentes en inventario. */
    private Integer stockDisponible;
    /** Lo maximo que se puede entregar ahora: min(pendiente, disponible). */
    private Integer puedeEntregar;
    /** Lotes vigentes ordenados por vencimiento (FEFO). */
    private List<LoteRs> lotesVigentes = new ArrayList<>();
    /** Lotes vencidos con existencias (no se pueden dispensar). */
    private List<LoteRs> lotesVencidos = new ArrayList<>();
    /** Sugerencia de lotes a usar (FEFO). */
    private List<ConfirmarDespachoRq.LoteCantidad> sugerencia = new ArrayList<>();
    /** Mensaje para el usuario (sin existencias, entrega parcial...). */
    private String alerta;
}
