package com.uniminuto.clinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniminuto.clinica.entity.FormulaMedica;

/**
 * Repositorio encargado de realizar las operaciones de acceso
 * a los datos de las fórmulas médicas.
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Obtiene todas las fórmulas médicas ordenadas desde la más reciente
     * hasta la más antigua según la fecha de creación.
     *
     * @return lista de fórmulas médicas ordenadas por fecha descendente.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}