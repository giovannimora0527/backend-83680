package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.GenerarFacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de facturacion (Administrador y Recepcion).
 */
@Tag(name = "Facturación", description = "Facturas, pagos, anulación y PDF")
@RequestMapping("/factura")
public interface FacturaApi {

    @Operation(summary = "Citas atendidas pendientes de facturar")
    @GetMapping(value = "/por-facturar", produces = "application/json")
    ResponseEntity<List<CitaRs>> citasPorFacturar();

    @Operation(summary = "Vista previa de la factura de una cita")
    @GetMapping(value = "/previsualizar", produces = "application/json")
    ResponseEntity<FacturaRs> previsualizar(@RequestParam Long citaId);

    @Operation(summary = "Generar factura de una cita atendida")
    @PostMapping(value = "/generar", produces = "application/json", consumes = "application/json")
    ResponseEntity<FacturaRs> generar(@RequestBody GenerarFacturaRq rq);

    @Operation(summary = "Historial de facturación (filtros por propietario, mascota y estado)")
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<FacturaRs>> listar(@RequestParam(required = false) Long clienteId,
                                           @RequestParam(required = false) Integer mascotaId,
                                           @RequestParam(required = false) String estado);

    @Operation(summary = "Detalle de una factura con pagos y trazabilidad")
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<FacturaRs> consultar(@RequestParam Long facturaId);

    @Operation(summary = "Registrar un pago")
    @PostMapping(value = "/pago", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> registrarPago(@RequestBody PagoRq rq);

    @Operation(summary = "Anular factura (Administrador, con motivo; no borra registros)")
    @PutMapping(value = "/anular", produces = "application/json")
    ResponseEntity<MiRespuestaRS> anular(@RequestParam Long facturaId, @RequestParam String motivo);

    @Operation(summary = "Factura en PDF")
    @GetMapping(value = "/pdf")
    ResponseEntity<byte[]> facturaPdf(@RequestParam Long facturaId);
}
