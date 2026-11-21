package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.ReporteFinancieroRS;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS;
import com.uniminuto.clinica.models.ReporteInventarioRS;

import java.time.LocalDate;

/** Define las operaciones de generación y exportación de reportes administrativos, financieros, de inventario y médicos. */
public interface ReporteService {

    /** Genera el reporte financiero (Sección 4.1). */
    ReporteFinancieroRS generarReporteFinanciero(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId);

    /** Genera el reporte de inventario y Kardex de movimientos (Sección 4.2). */
    ReporteInventarioRS generarReporteInventario(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicamentoId, Integer diasProximoVencer);

    /** Genera el reporte de gestión médica y atención de mascotas (Sección 4.3). */
    ReporteGestionMedicaRS generarReporteGestionMedica(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId);

    /** Valida si el rol del usuario tiene permiso para consultar o exportar el tipo de reporte indicado (Sección 4.4). */
    void validarPermisoReporte(String rol, String tipoReporte);

    /** Exporta a Excel (.xlsx) el reporte indicado con encabezados institucionales, fecha de generación y totales (Sección 4.4). */
    byte[] exportarReporteExcel(
            String tipoReporte, LocalDate fechaInicial, LocalDate fechaFinal,
            Long medicoId, Long especializacionId, Long medicamentoId,
            Integer diasProximoVencer, String agrupacion, String rol);

    /** Exporta a PDF (.pdf) el reporte indicado con encabezados institucionales, fecha de generación y totales (Sección 4.4). */
    byte[] exportarReportePdf(
            String tipoReporte, LocalDate fechaInicial, LocalDate fechaFinal,
            Long medicoId, Long especializacionId, Long medicamentoId,
            Integer diasProximoVencer, String agrupacion, String rol);
}
