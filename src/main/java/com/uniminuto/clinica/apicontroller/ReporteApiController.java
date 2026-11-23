package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ReporteApi;
import com.uniminuto.clinica.models.ReporteRs;
import com.uniminuto.clinica.service.ReporteService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador de reportes, solo delega al servicio.
 */
@RestController
public class ReporteApiController implements ReporteApi {

    @Autowired
    private ReporteService reporteService;

    @Override
    public ResponseEntity<List<Map<String, String>>> tipos() {
        return ResponseEntity.ok(reporteService.tipos());
    }

    @Override
    public ResponseEntity<ReporteRs> generar(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro) {
        return ResponseEntity.ok(reporteService.generar(tipo, desde, hasta, agrupacion, filtro));
    }

    @Override
    public ResponseEntity<byte[]> excel(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro) {
        return Descarga.archivo(reporteService.excel(tipo, desde, hasta, agrupacion, filtro),
                "reporte_" + tipo + ".xlsx", Descarga.EXCEL);
    }

    @Override
    public ResponseEntity<byte[]> pdf(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro) {
        return Descarga.archivo(reporteService.pdf(tipo, desde, hasta, agrupacion, filtro),
                "reporte_" + tipo + ".pdf", Descarga.PDF);
    }
}
