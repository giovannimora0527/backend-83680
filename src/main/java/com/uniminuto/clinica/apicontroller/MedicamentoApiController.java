package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoRs;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de medicamentos, solo delega a los servicios.
 */
@RestController
public class MedicamentoApiController implements MedicamentoApi {

    @Autowired
    private MedicamentoService medicamentoService;
    @Autowired
    private InventarioService inventarioService;

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos() {
        return ResponseEntity.ok(this.medicamentoService.obtenerMedicamentos());
    }

    @Override
    public ResponseEntity<List<MedicamentoRs>> listarInventario() {
        return ResponseEntity.ok(inventarioService.listarInventario());
    }

    @Override
    public ResponseEntity<MedicamentoRs> consultarMedicamento(Long medicamentoId) {
        return ResponseEntity.ok(inventarioService.consultarMedicamento(medicamentoId));
    }

    @Override
    public ResponseEntity<List<MovimientoRs>> listarMovimientos(Long medicamentoId) {
        return ResponseEntity.ok(inventarioService.listarMovimientos(medicamentoId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarMedicamento(MedicamentoRq rq) {
        return ResponseEntity.ok(inventarioService.guardarMedicamento(rq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarMedicamento(MedicamentoRq rq) {
        return ResponseEntity.ok(inventarioService.actualizarMedicamento(rq));
    }
}
