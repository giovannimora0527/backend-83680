package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Especializacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecializacionRepository extends JpaRepository<Especializacion, Long> {

    boolean existsByNombre(String nombre);

    boolean existsByCodigoEspecializacion(String codigoEspecializacion);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    boolean existsByCodigoEspecializacionAndIdNot(String codigoEspecializacion, Long id);
}