package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.ReporteFinancieroRS;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS;
import com.uniminuto.clinica.models.ReporteInventarioRS;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/reporte")
public interface ReporteApi {

    /**
     * Genera el reporte financiero y contable (Sección 4.1) filtrado por rango de fechas, veterinario y especialidad.
     */
    @GetMapping(value = "/financiero", produces = "application/json")
    ResponseEntity<ReporteFinancieroRS> obtenerReporteFinanciero(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinal,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) Long especializacionId);

    /**
     * Genera el reporte de inventario, valoración costo vs venta, alertas de stock/vencimiento y Kardex (Sección 4.2).
     */
    @GetMapping(value = "/inventario", produces = "application/json")
    ResponseEntity<ReporteInventarioRS> obtenerReporteInventario(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinal,
            @RequestParam(required = false) Long medicamentoId,
            @RequestParam(required = false) Integer diasProximoVencer);

    /**
     * Genera el reporte de gestión médica: citas por estado, consultas por veterinario/especialidad y mascotas atendidas (Sección 4.3).
     */
    @GetMapping(value = "/gestion-medica", produces = "application/json")
    ResponseEntity<ReporteGestionMedicaRS> obtenerReporteGestionMedica(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFinal,
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) Long especializacionId);
}
