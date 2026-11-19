package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ReporteApi;
import com.uniminuto.clinica.models.ReporteFinancieroRS;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS;
import com.uniminuto.clinica.models.ReporteInventarioRS;
import com.uniminuto.clinica.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
/** Controlador REST que expone los reportes administrativos, contables, de inventario y de gestión médica. */
public class ReporteApiController implements ReporteApi {

    @Autowired
    private ReporteService reporteService;

    @Override
    public ResponseEntity<ReporteFinancieroRS> obtenerReporteFinanciero(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId) {
        return ResponseEntity.ok(
                reporteService.generarReporteFinanciero(fechaInicial, fechaFinal, medicoId, especializacionId));
    }

    @Override
    public ResponseEntity<ReporteInventarioRS> obtenerReporteInventario(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicamentoId, Integer diasProximoVencer) {
        return ResponseEntity.ok(
                reporteService.generarReporteInventario(fechaInicial, fechaFinal, medicamentoId, diasProximoVencer));
    }

    @Override
    public ResponseEntity<ReporteGestionMedicaRS> obtenerReporteGestionMedica(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId) {
        return ResponseEntity.ok(
                reporteService.generarReporteGestionMedica(fechaInicial, fechaFinal, medicoId, especializacionId));
    }
}
