package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a los datos de los médicos.
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
