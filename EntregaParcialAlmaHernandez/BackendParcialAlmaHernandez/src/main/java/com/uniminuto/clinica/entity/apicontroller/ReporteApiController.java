package com.uniminuto.clinica.entity.apicontroller;

import com.uniminuto.clinica.api.ReporteApi;
import com.uniminuto.clinica.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
public class ReporteApiController implements ReporteApi {

    @Autowired
    private ReporteService reporteService;

    @Override
    @PreAuthorize("hasAnyRole('Administrador','Recepcion','Medico')")
    public ResponseEntity<Map<String, Object>> resumen(LocalDate inicio, LocalDate fin) {
        return ResponseEntity.ok(reporteService.resumen(inicio, fin));
    }

    @Override
    @PreAuthorize("hasAnyRole('Administrador','Recepcion','Medico')")
    public ResponseEntity<byte[]> excel(LocalDate inicio, LocalDate fin) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reporteService.excel(inicio, fin));
    }

    @Override
    @PreAuthorize("hasAnyRole('Administrador','Recepcion','Medico')")
    public ResponseEntity<byte[]> pdf(LocalDate inicio, LocalDate fin) throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reporteService.pdf(inicio, fin));
    }
}
