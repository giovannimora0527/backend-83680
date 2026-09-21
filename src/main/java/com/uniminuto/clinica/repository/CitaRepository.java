package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de acceso a datos de la entidad Cita.
 * Extiende de JpaRepository para obtener operaciones CRUD básicas.
 *
 * @author Alma Hernandez
 */

@Repository

public interface CitaRepository extends JpaRepository<Cita, Long> {
    /**
     * Busca todas las citas dentro de un rango de fechas específico
     * y las ordena de la más reciente a la más antigua (orden descendente).
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin);
}