package com.uniminuto.clinica.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniminuto.clinica.entity.Cita;

/**
 * Repositorio encargado de gestionar los datos de las citas.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca las citas que se encuentran entre una fecha inicial y una fecha final,
     * ordenándolas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return lista de citas encontradas y ordenadas por fecha descendente.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    );
}