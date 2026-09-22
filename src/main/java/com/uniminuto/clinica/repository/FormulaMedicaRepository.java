package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio JPA encargado de la comunicación con la base de datos para la entidad FormulaMedica.
 * Gestiona el inventario y persistencia de las fórmulas médicas[cite: 17].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /**
     * Consulta las formulas medicas ordenadas de la mas reciente a la mas antigua[cite: 17].
     *
     * @return Lista de formulas medicas ordenadas descendente por fecha de creación[cite: 17].
     */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}