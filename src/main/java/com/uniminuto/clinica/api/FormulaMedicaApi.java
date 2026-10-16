package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {
    /**
     * Registra una nueva fórmula médica.
     *
     * @param formulaRq datos de la fórmula.
     * @return respuesta con el resultado.
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearFormulaMedica(@RequestBody FormulaMedicaRq formulaRq);

    /**
     * Lista todas las fórmulas médicas, de la más reciente a la más antigua.
     *
     * @return lista de fórmulas médicas.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<FormulaMedica>> listarFormulasMedicas();

    /**
     * Consulta el detalle completo de una fórmula médica.
     *
     * @param formulaId identificador de la fórmula.
     * @return fórmula encontrada.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<FormulaMedica> consultarFormulaMedica(@RequestParam Long formulaId);

    /**
     * Actualiza los datos permitidos de una fórmula médica activa.
     *
     * @param formulaRq datos de la fórmula.
     * @return respuesta con el resultado.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarFormulaMedica(@RequestBody FormulaMedicaRq formulaRq);

    /**
     * Inactiva una fórmula médica; las fórmulas no se eliminan físicamente.
     *
     * @param formulaId identificador de la fórmula.
     * @return respuesta con el resultado.
     */
    @PatchMapping(value = "/inactivar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> inactivarFormulaMedica(@RequestParam Long formulaId);
}
