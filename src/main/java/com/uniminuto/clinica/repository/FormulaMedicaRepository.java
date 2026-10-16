package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Permite consultar y guardar fórmulas médicas. */
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {
    /** Indica si ya existe el medicamento en la cita indicada. */
    boolean existsByCita_IdAndMedicamento_Id(Long citaId, Long medicamentoId);

    /** Indica si el medicamento ya está en otra fórmula de la misma cita (se usa al editar). */
    boolean existsByCita_IdAndMedicamento_IdAndIdNot(Long citaId, Long medicamentoId, Long formulaId);

    /** Lista las fórmulas desde la más reciente a la más antigua. */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}
