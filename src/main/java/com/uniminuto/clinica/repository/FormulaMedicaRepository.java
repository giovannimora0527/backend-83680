package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla formula_medica.
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Formulas ordenadas por fecha de creacion, de la mas reciente a la mas antigua.
     *
     * @return formulas ordenadas.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();

    /**
     * Revisa si ya hay una formula activa con el mismo medicamento para la misma cita.
     *
     * @param citaId        id de la cita.
     * @param medicamentoId id del medicamento.
     * @return true si ya existe.
     */
    boolean existsByCitaIdAndMedicamentoIdAndActivoTrue(Long citaId, Long medicamentoId);
}
