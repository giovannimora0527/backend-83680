package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.ConfirmarDespachoRq;
import com.uniminuto.clinica.models.DespachoFormulaRs;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Rutas del despacho de medicamentos (perfil DESPACHO_MEDICO; la reversion la hace el Administrador).
 */
@Tag(name = "Despacho de medicamentos", description = "Fórmulas pendientes, entrega por lotes, reversión y comprobante")
@RequestMapping("/despacho")
public interface DespachoApi {

    @Operation(summary = "Fórmulas pendientes de despacho (buscar por número, propietario, mascota o fecha)")
    @GetMapping(value = "/pendientes", produces = "application/json")
    ResponseEntity<List<FormulaMedicaRs>> listarPendientes(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha);

    @Operation(summary = "Detalle de la fórmula para despacho: cantidades, existencias y lotes")
    @GetMapping(value = "/formula", produces = "application/json")
    ResponseEntity<DespachoFormulaRs> consultarFormula(@RequestParam Long formulaId);

    @Operation(summary = "Pasar la fórmula a EN_PREPARACION")
    @PutMapping(value = "/preparar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> iniciarPreparacion(@RequestParam Long formulaId);

    @Operation(summary = "Confirmar la entrega (descuenta inventario en una transacción)")
    @PostMapping(value = "/confirmar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> confirmarDespacho(@RequestBody ConfirmarDespachoRq rq);

    @Operation(summary = "Marcar la fórmula como pendiente por disponibilidad")
    @PutMapping(value = "/sin-disponibilidad", produces = "application/json")
    ResponseEntity<MiRespuestaRS> marcarSinDisponibilidad(@RequestParam Long formulaId,
                                                          @RequestParam(required = false) String observacion);

    @Operation(summary = "Cancelar el despacho de una fórmula (requiere motivo)")
    @PutMapping(value = "/cancelar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> cancelarDespacho(@RequestParam Long formulaId, @RequestParam String motivo);

    @Operation(summary = "Revertir un despacho confirmado (solo Administrador, requiere motivo)")
    @PutMapping(value = "/revertir", produces = "application/json")
    ResponseEntity<MiRespuestaRS> revertirDespacho(@RequestParam Long despachoId, @RequestParam String motivo);

    @Operation(summary = "Historial de despachos (log de despacho de fórmulas)")
    @GetMapping(value = "/historial", produces = "application/json")
    ResponseEntity<List<DespachoRs>> historial(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta);

    @Operation(summary = "Comprobante de despacho en PDF")
    @GetMapping(value = "/comprobante-pdf")
    ResponseEntity<byte[]> comprobantePdf(@RequestParam Long despachoId);
}
