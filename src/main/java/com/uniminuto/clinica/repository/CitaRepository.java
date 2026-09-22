package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la gestión de persistencia de la entidad Cita.
 * Permite ejecutar operaciones CRUD y consultas avanzadas sobre las citas programadas[cite: 16].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Filtra citas entre dos fechas ordenadas de la mas reciente a la mas antigua[cite: 16].
     *
     * @param fechaInicio Fecha inicial del filtro de citas[cite: 16]
     * @param fechaFin Fecha final del filtro de citas[cite: 16]
     * @return Lista de citas contenidas en el rango de fechas, ordenadas de forma descendente[cite: 16]
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}