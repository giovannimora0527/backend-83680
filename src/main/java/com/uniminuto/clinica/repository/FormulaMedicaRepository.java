package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();
}
