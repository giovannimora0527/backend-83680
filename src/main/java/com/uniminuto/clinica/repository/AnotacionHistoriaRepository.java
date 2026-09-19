package com.uniminuto.clinica.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;

/**
 * Repositorio encargado de gestionar los datos de las anotaciones
 * de las historias médicas.
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Busca las anotaciones que se encuentran dentro de un rango de fechas
     * y las ordena desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return lista de anotaciones ordenadas por fecha descendente.
     */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    );
}