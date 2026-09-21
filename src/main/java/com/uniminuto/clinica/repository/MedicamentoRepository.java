package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a los datos de los medicamentos.
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}
