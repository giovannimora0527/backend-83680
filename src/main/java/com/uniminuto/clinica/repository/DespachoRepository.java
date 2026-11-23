package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Despacho;
import com.uniminuto.clinica.entity.DespachoDetalle;
import com.uniminuto.clinica.entity.DespachoEstadoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de despachos, sus detalles y su log de estados.
 */
@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    /** @return despachos de una formula. */
    List<Despacho> findByFormulaIdOrderByFechaDesc(Long formulaId);

    /** @return despachos entre dos fechas. */
    List<Despacho> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);

    /** @return detalles de un despacho. */
    @Query("SELECT d FROM DespachoDetalle d WHERE d.despachoId = :despachoId ORDER BY d.id")
    List<DespachoDetalle> findDetalles(Long despachoId);

    /** @return log de estados de una formula. */
    @Query("SELECT l FROM DespachoEstadoLog l WHERE l.formulaId = :formulaId ORDER BY l.fecha DESC, l.id DESC")
    List<DespachoEstadoLog> findLog(Long formulaId);
}
