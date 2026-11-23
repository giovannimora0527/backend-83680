package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.DespachoEstadoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio del log de estados del despacho.
 */
@Repository
public interface DespachoEstadoLogRepository extends JpaRepository<DespachoEstadoLog, Long> {
}
