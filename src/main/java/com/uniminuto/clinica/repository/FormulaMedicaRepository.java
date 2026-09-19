package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Permite consultar y guardar fórmulas médicas. */
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {
    /** Lista las fórmulas desde la más reciente a la más antigua. */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}
