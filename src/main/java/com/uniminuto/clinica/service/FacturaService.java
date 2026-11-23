package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.FacturaRs;
import com.uniminuto.clinica.models.GenerarFacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoRq;

import java.util.List;

/**
 * Facturacion de servicios veterinarios.
 */
public interface FacturaService {

    /** @return citas atendidas que aun no tienen factura. */
    List<CitaRs> citasPorFacturar();

    /** @return vista previa de la factura de una cita (no guarda). */
    FacturaRs previsualizar(Long citaId);

    /** @return factura generada. */
    FacturaRs generar(GenerarFacturaRq rq);

    /** @return facturas filtradas por propietario, mascota y estado (historial). */
    List<FacturaRs> listar(Long clienteId, Integer mascotaId, String estado);

    /** @return factura con lineas, pagos y log. */
    FacturaRs consultar(Long facturaId);

    /** @return respuesta al registrar un pago. */
    MiRespuestaRS registrarPago(PagoRq rq);

    /** @return respuesta al anular (solo Administrador, con motivo). */
    MiRespuestaRS anular(Long facturaId, String motivo);

    /** @return PDF de la factura. */
    byte[] facturaPdf(Long facturaId);
}
