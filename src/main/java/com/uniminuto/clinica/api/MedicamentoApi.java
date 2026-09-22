package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Interfaz encargada de exponer los servicios web relacionados con los medicamentos disponibles en la clínica.
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    /**
     * Consulta y retorna el catálogo completo de medicamentos registrados en el inventario.
     *
     * @return ResponseEntity con la lista de objetos Medicamento en formato JSON.
     * @throws BadRequestException si ocurre un fallo al consultar los registros en la base de datos.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Medicamento>> listarMedicamentos()
            throws BadRequestException;
}