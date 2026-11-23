package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.AtencionInfoRs;
import com.uniminuto.clinica.models.AtencionRq;
import com.uniminuto.clinica.models.AtencionRs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Rutas de la atencion medica (solo perfil Medico).
 */
@Tag(name = "Atención médica", description = "Atención de la cita, historia clínica, fórmula y prefactura")
@RequestMapping("/atencion")
public interface AtencionApi {

    @Operation(summary = "Datos para atender una cita: mascota, antecedentes y servicios")
    @GetMapping(value = "/cita", produces = "application/json")
    ResponseEntity<AtencionInfoRs> consultarAtencion(@RequestParam Long citaId);

    @Operation(summary = "Finalizar la atención: historia clínica + fórmula + cita atendida")
    @PostMapping(value = "/finalizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<AtencionRs> finalizarAtencion(@RequestBody AtencionRq rq);

    @Operation(summary = "Prefactura en PDF con servicios y medicamentos formulados")
    @GetMapping(value = "/prefactura-pdf")
    ResponseEntity<byte[]> prefacturaPdf(@RequestParam Long citaId);

    @Operation(summary = "Fórmula médica en PDF")
    @GetMapping(value = "/formula-pdf")
    ResponseEntity<byte[]> formulaPdf(@RequestParam Long formulaId);
}
