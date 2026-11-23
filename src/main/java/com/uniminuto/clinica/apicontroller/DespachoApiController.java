package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.DespachoApi;
import com.uniminuto.clinica.models.ConfirmarDespachoRq;
import com.uniminuto.clinica.models.DespachoFormulaRs;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.DespachoService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador del despacho, solo delega al servicio.
 */
@RestController
public class DespachoApiController implements DespachoApi {

    @Autowired
    private DespachoService despachoService;

    @Override
    public ResponseEntity<List<FormulaMedicaRs>> listarPendientes(String buscar, LocalDate fecha) {
        return ResponseEntity.ok(despachoService.listarPendientes(buscar, fecha));
    }

    @Override
    public ResponseEntity<DespachoFormulaRs> consultarFormula(Long formulaId) {
        return ResponseEntity.ok(despachoService.consultarFormula(formulaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> iniciarPreparacion(Long formulaId) {
        return ResponseEntity.ok(despachoService.iniciarPreparacion(formulaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> confirmarDespacho(ConfirmarDespachoRq rq) {
        return ResponseEntity.ok(despachoService.confirmarDespacho(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> marcarSinDisponibilidad(Long formulaId, String observacion) {
        return ResponseEntity.ok(despachoService.marcarSinDisponibilidad(formulaId, observacion));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cancelarDespacho(Long formulaId, String motivo) {
        return ResponseEntity.ok(despachoService.cancelarDespacho(formulaId, motivo));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> revertirDespacho(Long despachoId, String motivo) {
        return ResponseEntity.ok(despachoService.revertirDespacho(despachoId, motivo));
    }

    @Override
    public ResponseEntity<List<DespachoRs>> historial(LocalDate desde, LocalDate hasta) {
        return ResponseEntity.ok(despachoService.historial(desde, hasta));
    }

    @Override
    public ResponseEntity<byte[]> comprobantePdf(Long despachoId) {
        return Descarga.archivo(despachoService.comprobantePdf(despachoId), "despacho_" + despachoId + ".pdf", Descarga.PDF);
    }
}
