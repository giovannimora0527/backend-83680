package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de la tabla movimiento_inventario.
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /**
     * Historial de un medicamento, del mas reciente al mas antiguo.
     *
     * @param medicamentoId id del medicamento.
     * @return movimientos.
     */
    List<MovimientoInventario> findByMedicamentoIdOrderByFechaDescIdDesc(Long medicamentoId);

    /**
     * Movimientos entre dos fechas.
     *
     * @param inicio inicio.
     * @param fin    fin.
     * @return movimientos.
     */
    List<MovimientoInventario> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Movimientos de un despacho.
     *
     * @param despachoId id del despacho.
     * @return movimientos.
     */
    List<MovimientoInventario> findByDespachoId(Long despachoId);
}
