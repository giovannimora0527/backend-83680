package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a los datos de las fórmulas médicas.
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Obtiene todas las fórmulas médicas ordenadas por fecha de creación,
     * de la más reciente a la más antigua.
     *
     * @return la lista de fórmulas médicas ordenada.
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}
