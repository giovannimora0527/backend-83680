package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.entity.TarifaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la tabla tarifa.
 */
@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    /** @return tarifas ordenadas por tipo y nombre. */
    List<Tarifa> findAllByOrderByTipoAscNombreAsc();

    /** @return tarifa con ese codigo. */
    Optional<Tarifa> findByCodigoIgnoreCase(String codigo);

    /**
     * Logs de una tarifa, del mas reciente al mas antiguo.
     *
     * @param tarifaId id de la tarifa.
     * @return historial.
     */
    @Query("SELECT l FROM TarifaLog l WHERE l.tarifaId = :tarifaId ORDER BY l.fecha DESC, l.id DESC")
    List<TarifaLog> findLogs(Long tarifaId);
}
