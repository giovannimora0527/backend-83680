package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de acceso a datos de la entidad FormulaMedica.
 * Extiende de JpaRepository para obtener operaciones CRUD básicas.
 *
 * @author Alma Hernandez
 */

@Repository

public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {
    /**
     * Busca todas las fórmulas médicas ordenadas por fecha de creación
     * de la más reciente a la más antigua (orden descendente).
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}