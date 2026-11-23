package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.ReporteRs;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Reportes administrativos, contables, de inventario y de gestion medica.
 */
public interface ReporteService {

    /**
     * Tipos de reporte disponibles agrupados por categoria.
     *
     * @return lista de {codigo, nombre, categoria}.
     */
    List<Map<String, String>> tipos();

    /**
     * Genera los datos de un reporte.
     *
     * @param tipo       codigo del reporte.
     * @param desde      fecha inicial.
     * @param hasta      fecha final.
     * @param agrupacion DIA, MES o ANIO (reportes por periodo).
     * @param filtro     filtro adicional segun el reporte (estado, alerta...).
     * @return reporte.
     */
    ReporteRs generar(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro);

    /** @return reporte en Excel. */
    byte[] excel(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro);

    /** @return reporte en PDF. */
    byte[] pdf(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro);
}
