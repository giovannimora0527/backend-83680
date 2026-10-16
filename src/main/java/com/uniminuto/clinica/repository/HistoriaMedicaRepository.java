package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Permite consultar y guardar historias médicas. */
public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {
    /** Indica si la cita ya tiene una historia médica. */
    boolean existsByCita_Id(Long citaId);

    /** Lista las historias desde la consulta más reciente a la más antigua. */
    List<HistoriaMedica> findAllByOrderByFechaHoraDesc();
}
