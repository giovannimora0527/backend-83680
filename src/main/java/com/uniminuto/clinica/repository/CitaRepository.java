package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para acceso a datos de citas.
 */
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca citas dentro de un rango de fechas y las ordena de forma descendente.
     *
     * @param fechaInicial inicio del rango.
     * @param fechaFinal fin del rango.
     * @return citas encontradas.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    );
}
