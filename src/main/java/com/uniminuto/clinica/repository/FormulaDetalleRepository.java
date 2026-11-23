package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla formula_detalle.
 */
@Repository
public interface FormulaDetalleRepository extends JpaRepository<FormulaDetalle, Long> {

    /** @return detalles de una formula. */
    List<FormulaDetalle> findByFormulaIdOrderByIdAsc(Long formulaId);
}
