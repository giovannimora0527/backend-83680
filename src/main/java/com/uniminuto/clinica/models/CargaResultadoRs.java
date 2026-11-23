package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.List;

/**
 * Resultado de la vista previa o de la confirmacion de una carga masiva.
 */
@Data
public class CargaResultadoRs {
    private String nombreArchivo;
    private Integer totalFilas;
    private Integer filasValidas;
    private Integer filasError;
    /** Filas importadas (solo al confirmar). */
    private Integer filasImportadas;
    /** Id del log de la carga (solo al confirmar). */
    private Long logId;
    private List<CargaFilaRs> filas;
}
