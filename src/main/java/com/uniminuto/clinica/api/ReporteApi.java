package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.ReporteRs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Rutas del modulo de reportes (solo Administrador).
 */
@Tag(name = "Reportes", description = "Reportes financieros, de inventario y médicos en PDF y Excel")
@RequestMapping("/reporte")
public interface ReporteApi {

    @Operation(summary = "Tipos de reporte disponibles")
    @GetMapping(value = "/tipos", produces = "application/json")
    ResponseEntity<List<Map<String, String>>> tipos();

    @Operation(summary = "Ver los resultados de un reporte antes de exportar")
    @GetMapping(value = "/datos", produces = "application/json")
    ResponseEntity<ReporteRs> generar(@RequestParam String tipo,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                      @RequestParam(required = false) String agrupacion,
                                      @RequestParam(required = false) String filtro);

    @Operation(summary = "Descargar el reporte en Excel")
    @GetMapping(value = "/excel")
    ResponseEntity<byte[]> excel(@RequestParam String tipo,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                 @RequestParam(required = false) String agrupacion,
                                 @RequestParam(required = false) String filtro);

    @Operation(summary = "Descargar el reporte en PDF")
    @GetMapping(value = "/pdf")
    ResponseEntity<byte[]> pdf(@RequestParam String tipo,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                               @RequestParam(required = false) String agrupacion,
                               @RequestParam(required = false) String filtro);
}
