package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/** Permite consultar y guardar médicos. */
public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
