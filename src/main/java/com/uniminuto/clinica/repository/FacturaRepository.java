package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FacturaDetalle;
import com.uniminuto.clinica.entity.FacturaLog;
import com.uniminuto.clinica.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de facturas con consultas de sus detalles, pagos y log.
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    /** @return facturas de la mas reciente a la mas antigua. */
    List<Factura> findAllByOrderByFechaDesc();

    /** @return facturas entre dos fechas. */
    List<Factura> findByFechaBetweenOrderByFechaAsc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Revisa si una cita ya tiene una factura que no este anulada (no cobrar dos veces).
     *
     * @param citaId id de la cita.
     * @param estado estado a excluir (ANULADA).
     * @return true si existe.
     */
    boolean existsByCitaIdAndEstadoNot(Long citaId, String estado);

    /** @return lineas de una factura. */
    @Query("SELECT d FROM FacturaDetalle d WHERE d.facturaId = :facturaId ORDER BY d.id")
    List<FacturaDetalle> findDetalles(Long facturaId);

    /** @return pagos de una factura. */
    @Query("SELECT p FROM Pago p WHERE p.facturaId = :facturaId ORDER BY p.fecha")
    List<Pago> findPagos(Long facturaId);

    /** @return pagos entre dos fechas. */
    @Query("SELECT p FROM Pago p WHERE p.fecha BETWEEN :inicio AND :fin ORDER BY p.fecha")
    List<Pago> findPagosEntre(LocalDateTime inicio, LocalDateTime fin);

    /** @return log de una factura. */
    @Query("SELECT l FROM FacturaLog l WHERE l.facturaId = :facturaId ORDER BY l.fecha, l.id")
    List<FacturaLog> findLog(Long facturaId);

    /** @return lineas de varias facturas (reportes). */
    @Query("SELECT d FROM FacturaDetalle d WHERE d.facturaId IN :ids")
    List<FacturaDetalle> findDetallesDe(List<Long> ids);
}
