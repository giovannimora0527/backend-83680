package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {
    List<HistoriaMedica> findAllByOrderByFechaCreacionDesc();
}
