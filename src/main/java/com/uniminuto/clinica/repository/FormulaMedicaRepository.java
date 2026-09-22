package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio para acceso a datos de formulas medicas.
 */
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Obtiene todas las fórmulas ordenadas de la más reciente a la más antigua.
     *
     * @return lista ordenada.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}
