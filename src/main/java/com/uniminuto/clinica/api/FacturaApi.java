package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoFacturaRq;
import com.uniminuto.clinica.models.PreliquidacionFacturaRS;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/factura")
public interface FacturaApi {

    /**
     * Lista las facturas aplicando filtros opcionales por propietario, mascota, estado y rango de fechas.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Factura>> listarFacturas(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Integer mascotaId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal);

    /**
     * Consulta una factura por su identificador incluyendo sus detalles y pagos.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<Factura> consultarFactura(@RequestParam Long facturaId);

    /**
     * Preliquida una cita atendida devolviendo el servicio sugerido y los medicamentos efectivamente dispensados.
     */
    @GetMapping(value = "/preliquidar", produces = "application/json")
    ResponseEntity<PreliquidacionFacturaRS> preliquidarCita(@RequestParam Long citaId);

    /**
     * Consulta el historial de facturación de un propietario.
     */
    @GetMapping(value = "/historial-cliente", produces = "application/json")
    ResponseEntity<List<Factura>> listarHistorialPorCliente(@RequestParam Long clienteId);

    /**
     * Consulta el historial de facturación de una mascota.
     */
    @GetMapping(value = "/historial-mascota", produces = "application/json")
    ResponseEntity<List<Factura>> listarHistorialPorMascota(@RequestParam Integer mascotaId);

    /**
     * Genera una nueva factura para una cita atendida congelando precios históricos de servicios y medicamentos dispensados.
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearFactura(@RequestBody FacturaRq facturaRq);

    /**
     * Registra un pago o abono parcial asociado a una factura.
     */
    @PostMapping(value = "/{facturaId}/pagos", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> registrarPago(@PathVariable Long facturaId, @RequestBody PagoFacturaRq pagoRq);

    /**
     * Anula una factura existente sin eliminar su historial ni trazabilidad.
     */
    @PatchMapping(value = "/{facturaId}/anular", produces = "application/json")
    ResponseEntity<MiRespuestaRS> anularFactura(
            @PathVariable Long facturaId,
            @RequestParam String motivo,
            @RequestParam(required = false) String usuario);
}
