package com.uniminuto.clinica.entity.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.model.MedicamentoRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import com.uniminuto.clinica.model.CargaInventarioConfirmarRq;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MedicamentoApiController implements MedicamentoApi {

    @Autowired
    private MedicamentoService medicamentoService;

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos() {
        return ResponseEntity.ok(this.medicamentoService.listarMedicamentos());
    }

    @Override
    public ResponseEntity<RespuestaRs> guardarMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException {
        return ResponseEntity.ok(medicamentoService.guardarMedicamento(medicamentoRq));
    }

    @Override
    public ResponseEntity<RespuestaRs> actualizarMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException {
        return ResponseEntity.ok(medicamentoService.actualizarMedicamento(medicamentoRq));
    }

    @Override
    public ResponseEntity<Map<String, Object>> previsualizarCarga(MultipartFile archivo) throws Exception {
        return ResponseEntity.ok(medicamentoService.previsualizarCarga(archivo));
    }

    @Override
    public ResponseEntity<RespuestaRs> confirmarCarga(CargaInventarioConfirmarRq carga) throws Exception {
        return ResponseEntity.ok(medicamentoService.confirmarCarga(carga));
    }
}
