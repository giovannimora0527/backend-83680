package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Rutas de los servicios de los medicamentos.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    /**
     * Lista los medicamentos del inventario.
     *
     * @return los medicamentos registrados.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<Medicamento>> listarMedicamentos();
}
