package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de acceso a datos de la entidad HistoriaMedica.
 * Extiende de JpaRepository para obtener operaciones CRUD básicas.
 * Aunque no se expone directamente en los endpoints del parcial, este repositorio
 * es necesario para mantener la arquitectura por capas y permitir futuras
 * operaciones sobre historias médicas.
 *
 * @author Alma Hernandez
 */

@Repository

public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {
    /**
     * Busca una historia médica por el identificador del paciente.
     */
    HistoriaMedica findByPacienteId(Integer pacienteId);
}