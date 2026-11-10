package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.MovimientoMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/** Permite consultar y guardar movimientos de inventario. */
public interface MovimientoMedicamentoRepository extends JpaRepository<MovimientoMedicamento, Long> {

    /** Consulta el historial de movimientos por medicamento, del más reciente al más antiguo. */
    List<MovimientoMedicamento> findByMedicamentoIdOrderByFechaRegistroDesc(Long medicamentoId);

    /** Consulta los movimientos vinculados a una fórmula específica. */
    List<MovimientoMedicamento> findByMedicamentoIdAndReferenciaTipoAndReferenciaIdOrderByFechaRegistroDesc(
            Long medicamentoId, String referenciaTipo, Long referenciaId);
}
