package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ReporteApi;
import com.uniminuto.clinica.models.ReporteFinancieroRS;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS;
import com.uniminuto.clinica.models.ReporteInventarioRS;
import com.uniminuto.clinica.service.ReporteService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
/**
 * Controlador REST que expone los reportes administrativos, contables, de
 * inventario, de gestión médica y su exportación PDF/Excel.
 */
public class ReporteApiController implements ReporteApi {

        @Autowired
        private ReporteService reporteService;

        @Override
        public ResponseEntity<ReporteFinancieroRS> obtenerReporteFinanciero(
                        LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId,
                        String rol) {
                reporteService.validarPermisoReporte(rol, Constantes.REPORTE_FINANCIERO);
                return ResponseEntity.ok(
                                reporteService.generarReporteFinanciero(fechaInicial, fechaFinal, medicoId,
                                                especializacionId));
        }

        @Override
        public ResponseEntity<ReporteInventarioRS> obtenerReporteInventario(
                        LocalDate fechaInicial, LocalDate fechaFinal, Long medicamentoId, Integer diasProximoVencer,
                        String rol) {
                reporteService.validarPermisoReporte(rol, Constantes.REPORTE_INVENTARIO);
                return ResponseEntity.ok(
                                reporteService.generarReporteInventario(fechaInicial, fechaFinal, medicamentoId,
                                                diasProximoVencer));
        }

        @Override
        public ResponseEntity<ReporteGestionMedicaRS> obtenerReporteGestionMedica(
                        LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId,
                        String rol) {
                reporteService.validarPermisoReporte(rol, Constantes.REPORTE_MEDICO);
                return ResponseEntity.ok(
                                reporteService.generarReporteGestionMedica(fechaInicial, fechaFinal, medicoId,
                                                especializacionId));
        }

        @Override
        public ResponseEntity<byte[]> exportarReporteExcel(
                        String tipoReporte, LocalDate fechaInicial, LocalDate fechaFinal,
                        Long medicoId, Long especializacionId, Long medicamentoId,
                        Integer diasProximoVencer, String agrupacion, String rol) {
                byte[] archivo = reporteService.exportarReporteExcel(
                                tipoReporte, fechaInicial, fechaFinal, medicoId, especializacionId,
                                medicamentoId, diasProximoVencer, agrupacion, rol);
                String tipoLimpio = tipoReporte != null ? tipoReporte.toLowerCase() : "general";
                String nombreArchivo = "reporte_" + tipoLimpio + "_" + LocalDate.now() + ".xlsx";
                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + nombreArchivo + "\"")
                                .contentType(MediaType.parseMediaType(
                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(archivo);
        }

        @Override
        public ResponseEntity<byte[]> exportarReportePdf(
                        String tipoReporte, LocalDate fechaInicial, LocalDate fechaFinal,
                        Long medicoId, Long especializacionId, Long medicamentoId,
                        Integer diasProximoVencer, String agrupacion, String rol) {
                byte[] archivo = reporteService.exportarReportePdf(
                                tipoReporte, fechaInicial, fechaFinal, medicoId, especializacionId,
                                medicamentoId, diasProximoVencer, agrupacion, rol);
                String tipoLimpio = tipoReporte != null ? tipoReporte.toLowerCase() : "general";
                String nombreArchivo = "reporte_" + tipoLimpio + "_" + LocalDate.now() + ".pdf";
                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + nombreArchivo + "\"")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(archivo);
        }
}
