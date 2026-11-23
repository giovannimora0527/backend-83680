package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define las operaciones de acceso a datos
 * para la entidad HistoriaMedica.
 */
@Repository
public interface HistoriaMedicaRepository
        extends JpaRepository<HistoriaMedica, Long> {

    /**
     * Consulta las historias médicas dentro de un rango de fechas
     * y las ordena desde la más reciente hasta la más antigua.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de historias médicas dentro del rango.
     */
    List<HistoriaMedica> findByFechaCreacionBetweenOrderByFechaCreacionDesc(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin);
}