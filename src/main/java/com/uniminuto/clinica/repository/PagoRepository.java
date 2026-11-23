package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio: Pagos de facturas.
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
}
