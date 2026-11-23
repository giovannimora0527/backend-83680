package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FacturaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio: Lineas de factura.
 */
@Repository
public interface FacturaDetalleRepository extends JpaRepository<FacturaDetalle, Long> {
}
