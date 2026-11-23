package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de consulta de fórmulas médicas (se crean desde la atención de la cita).
 */
@Tag(name = "Fórmulas médicas", description = "Consulta, inactivación y PDF de fórmulas")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {

    @Operation(summary = "Listar fórmulas (el médico ve solo las suyas)")
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<FormulaMedicaRs>> listarFormulasMedicas();

    @Operation(summary = "Detalle de una fórmula con medicamentos y disponibilidad")
    @GetMapping(value = "/consultar", produces = {"application/json"})
    ResponseEntity<FormulaMedicaRs> consultarFormula(@RequestParam Long formulaId);

    @Operation(summary = "Inactivar fórmula (no se borra; solo si no se ha despachado)")
    @PatchMapping(value = "/inactivar", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> inactivarFormula(@RequestParam Long formulaId);

    @Operation(summary = "Fórmula médica en PDF")
    @GetMapping(value = "/pdf")
    ResponseEntity<byte[]> formulaPdf(@RequestParam Long formulaId);
}
