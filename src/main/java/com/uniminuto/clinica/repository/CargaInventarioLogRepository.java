package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.CargaInventarioLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio del log de cargas masivas.
 */
@Repository
public interface CargaInventarioLogRepository extends JpaRepository<CargaInventarioLog, Long> {

    /**
     * Cargas de la mas reciente a la mas antigua.
     *
     * @return logs.
     */
    List<CargaInventarioLog> findAllByOrderByFechaDesc();
}
