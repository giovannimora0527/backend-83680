package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * Consulta el detalle de una fórmula.
     *
     * @param formulaId id de la fórmula.
     * @return la fórmula.
     */
    @GetMapping(value = "/consultar", produces = {"application/json"})
    ResponseEntity<FormulaMedicaRs> consultarFormula(@RequestParam Long formulaId);

    /**
     * Crea una fórmula médica.
     *
     * @param formulaRq datos de la fórmula.
     * @return respuesta de éxito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarFormula(@RequestBody FormulaMedicaRq formulaRq);

    /**
     * Edita una fórmula (solo medicamento, dosis, frecuencia, duración, indicaciones y fecha).
     *
     * @param formulaRq datos de la fórmula con su id.
     * @return respuesta de éxito.
     */
    @PutMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarFormula(@RequestBody FormulaMedicaRq formulaRq);

    /**
     * Inactiva una fórmula. No se borra físicamente porque es información clínica.
     *
     * @param formulaId id de la fórmula.
     * @return respuesta de éxito.
     */
    @PatchMapping(value = "/inactivar", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> inactivarFormula(@RequestParam Long formulaId);
}
