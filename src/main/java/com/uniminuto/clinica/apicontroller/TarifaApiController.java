package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.TarifaApi;
import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.TarifaLogRs;
import com.uniminuto.clinica.models.TarifaRq;
import com.uniminuto.clinica.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de tarifas, solo delega al servicio.
 */
@RestController
public class TarifaApiController implements TarifaApi {

    @Autowired
    private TarifaService tarifaService;

    @Override
    public ResponseEntity<List<Tarifa>> listarTarifas() {
        return ResponseEntity.ok(tarifaService.listarTarifas());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarTarifa(TarifaRq rq) {
        return ResponseEntity.ok(tarifaService.guardarTarifa(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarTarifa(TarifaRq rq) {
        return ResponseEntity.ok(tarifaService.actualizarTarifa(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstado(Long id, Boolean activo) {
        return ResponseEntity.ok(tarifaService.cambiarEstado(id, activo));
    }

    @Override
    public ResponseEntity<List<TarifaLogRs>> listarLog(Long tarifaId) {
        return ResponseEntity.ok(tarifaService.listarLog(tarifaId));
    }
}
