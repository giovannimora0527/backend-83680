package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Permite consultar y guardar historias médicas. */
public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {
    /** Lista las historias desde la más reciente a la más antigua. */
    List<HistoriaMedica> findAllByOrderByFechaCreacionDesc();
}
