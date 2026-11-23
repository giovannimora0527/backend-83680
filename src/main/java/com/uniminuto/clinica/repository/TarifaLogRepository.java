package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.TarifaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio del log de tarifas.
 */
@Repository
public interface TarifaLogRepository extends JpaRepository<TarifaLog, Long> {
}
