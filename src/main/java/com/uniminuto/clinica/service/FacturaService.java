package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoFacturaRq;
import com.uniminuto.clinica.models.PreliquidacionFacturaRS;

import java.time.LocalDateTime;
import java.util.List;

/** Define las operaciones de negocio para facturación, abonos y anulación controlada. */
public interface FacturaService {
    /** Lista las facturas aplicando filtros opcionales. */
    List<Factura> listarFacturas(Long clienteId, Integer mascotaId, String estado,
                                 LocalDateTime fechaInicial, LocalDateTime fechaFinal);

    /** Consulta una factura por su identificador. */
    Factura consultarFactura(Long facturaId);

    /** Preliquida una cita atendida cargando servicios sugeridos y medicamentos efectivamente dispensados. */
    PreliquidacionFacturaRS preliquidarCita(Long citaId);

    /** Consulta el historial de facturación de un propietario. */
    List<Factura> listarHistorialPorCliente(Long clienteId);

    /** Consulta el historial de facturación de una mascota. */
    List<Factura> listarHistorialPorMascota(Integer mascotaId);

    /** Genera una factura sobre una cita atendida y medicamentos dispensados, congelando precios históricos. */
    MiRespuestaRS crearFactura(FacturaRq facturaRq);

    /** Registra un pago total o parcial asociado a una factura. */
    MiRespuestaRS registrarPago(Long facturaId, PagoFacturaRq pagoRq);

    /** Anula una factura sin eliminar su historial ni trazabilidad. */
    MiRespuestaRS anularFactura(Long facturaId, String motivo, String usuario);
}
