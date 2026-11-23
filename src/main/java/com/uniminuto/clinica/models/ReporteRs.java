package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de un reporte: el mismo objeto se muestra en pantalla y se exporta a PDF o Excel.
 */
@Data
public class ReporteRs {
    private String tipo;
    private String titulo;
    /** Filtros aplicados (rango de fechas, agrupacion...). */
    private String subtitulo;
    private List<String> columnas = new ArrayList<>();
    private List<List<Object>> filas = new ArrayList<>();
    /** Fila de totales (puede ir vacia). */
    private List<Object> totales = new ArrayList<>();
}
