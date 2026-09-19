package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/** Controlador que expone los medicamentos. */
public class MedicamentoApiController implements MedicamentoApi {

    @Autowired
    /** Servicio con la lógica de medicamentos. */
    private MedicamentoService medicamentoService;

    @Override
    /** Delega la consulta de medicamentos. */
    public ResponseEntity<List<Medicamento>> listarMedicamentos() throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.obtenerMedicamentos());
    }
}
