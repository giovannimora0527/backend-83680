package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/** Permite consultar y guardar medicamentos. */
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}
