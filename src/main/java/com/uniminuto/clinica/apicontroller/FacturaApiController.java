package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FacturaApi;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.GenerarFacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;
import com.uniminuto.clinica.service.FacturaService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de facturacion, solo delega al servicio.
 */
@RestController
public class FacturaApiController implements FacturaApi {

    @Autowired
    private FacturaService facturaService;

    @Override
    public ResponseEntity<List<CitaRs>> citasPorFacturar() {
        return ResponseEntity.ok(facturaService.citasPorFacturar());
    }

    @Override
    public ResponseEntity<FacturaRs> previsualizar(Long citaId) {
        return ResponseEntity.ok(facturaService.previsualizar(citaId));
    }

    @Override
    public ResponseEntity<FacturaRs> generar(GenerarFacturaRq rq) {
        return ResponseEntity.ok(facturaService.generar(rq));
    }

    @Override
    public ResponseEntity<List<FacturaRs>> listar(Long clienteId, Integer mascotaId, String estado) {
        return ResponseEntity.ok(facturaService.listar(clienteId, mascotaId, estado));
    }

    @Override
    public ResponseEntity<FacturaRs> consultar(Long facturaId) {
        return ResponseEntity.ok(facturaService.consultar(facturaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> registrarPago(PagoRq rq) {
        return ResponseEntity.ok(facturaService.registrarPago(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> anular(Long facturaId, String motivo) {
        return ResponseEntity.ok(facturaService.anular(facturaId, motivo));
    }

    @Override
    public ResponseEntity<byte[]> facturaPdf(Long facturaId) {
        return Descarga.archivo(facturaService.facturaPdf(facturaId), "factura_" + facturaId + ".pdf", Descarga.PDF);
    }
}
