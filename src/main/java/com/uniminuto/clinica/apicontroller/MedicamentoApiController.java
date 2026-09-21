package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los servicios de los medicamentos.
 */
@RestController
public class MedicamentoApiController implements MedicamentoApi {

    /**
     * Servicio con la logica de los medicamentos.
     */
    @Autowired
    private MedicamentoService medicamentoService;

    /**
     * Lista los medicamentos del inventario.
     *
     * @return los medicamentos registrados.
     */
    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos() {
        return ResponseEntity.ok(this.medicamentoService.obtenerMedicamentos());
    }
}
