package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.FormulaMedicaRs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Contrato de los servicios web de las fórmulas médicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    /**
     * Lista las fórmulas médicas ordenadas por fecha de creación,
     * de la más reciente a la más antigua.
     *
     * @return las fórmulas médicas registradas.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaMedicaRs>> listarFormulasMedicas();
}
