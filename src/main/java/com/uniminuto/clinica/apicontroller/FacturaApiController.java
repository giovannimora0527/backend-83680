package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FacturaApi;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoFacturaRq;
import com.uniminuto.clinica.models.PreliquidacionFacturaRS;
import com.uniminuto.clinica.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
/** Controlador REST que expone los servicios de facturación, pagos e historial por propietario y mascota. */
public class FacturaApiController implements FacturaApi {

    @Autowired
    private FacturaService facturaService;

    @Override
    public ResponseEntity<List<Factura>> listarFacturas(Long clienteId, Integer mascotaId, String estado,
                                                        LocalDateTime fechaInicial, LocalDateTime fechaFinal) {
        return ResponseEntity.ok(facturaService.listarFacturas(clienteId, mascotaId, estado, fechaInicial, fechaFinal));
    }

    @Override
    public ResponseEntity<Factura> consultarFactura(Long facturaId) {
        return ResponseEntity.ok(facturaService.consultarFactura(facturaId));
    }

    @Override
    public ResponseEntity<PreliquidacionFacturaRS> preliquidarCita(Long citaId) {
        return ResponseEntity.ok(facturaService.preliquidarCita(citaId));
    }

    @Override
    public ResponseEntity<List<Factura>> listarHistorialPorCliente(Long clienteId) {
        return ResponseEntity.ok(facturaService.listarHistorialPorCliente(clienteId));
    }

    @Override
    public ResponseEntity<List<Factura>> listarHistorialPorMascota(Integer mascotaId) {
        return ResponseEntity.ok(facturaService.listarHistorialPorMascota(mascotaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> crearFactura(FacturaRq facturaRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.crearFactura(facturaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> registrarPago(Long facturaId, PagoFacturaRq pagoRq) {
        return ResponseEntity.ok(facturaService.registrarPago(facturaId, pagoRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> anularFactura(Long facturaId, String motivo, String usuario) {
        return ResponseEntity.ok(facturaService.anularFactura(facturaId, motivo, usuario));
    }
}
