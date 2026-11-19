package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.PagoFactura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Permite consultar y registrar los pagos asociados a facturas. */
public interface PagoFacturaRepository extends JpaRepository<PagoFactura, Long> {
    /** Lista los pagos de una factura del más reciente al más antiguo. */
    List<PagoFactura> findByFactura_IdOrderByFechaPagoDesc(Long facturaId);
}
