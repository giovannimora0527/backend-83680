package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.CargaMasivaMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargaMasivaMedicamentoRepository extends JpaRepository<CargaMasivaMedicamento, Long> {
}
