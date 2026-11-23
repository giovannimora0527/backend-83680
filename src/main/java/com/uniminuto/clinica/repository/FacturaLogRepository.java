package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FacturaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio: Log de facturas.
 */
@Repository
public interface FacturaLogRepository extends JpaRepository<FacturaLog, Long> {
}
