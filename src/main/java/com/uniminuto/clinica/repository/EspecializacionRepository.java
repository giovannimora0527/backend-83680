package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Especializacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla especializacion.
 */
@Repository
public interface EspecializacionRepository extends JpaRepository<Especializacion, Long> {

    /**
     * Lista las especializaciones ordenadas por nombre.
     *
     * @return especializaciones ordenadas.
     */
    List<Especializacion> findAllByOrderByNombreAsc();
}
