package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Permite consultar los detalles de servicios y medicamentos facturados. */
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {
    /** Lista las líneas de detalle de una factura específica. */
    List<DetalleFactura> findByFactura_IdOrderByIdAsc(Long facturaId);
}
