package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para acceso a datos de anotaciones de historia.
 */
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Obtiene todas las anotaciones ordenadas de forma descendente por fecha.
     *
     * @return anotaciones ordenadas.
     */
    List<AnotacionHistoria> findAllByOrderByFechaDesc();

    /**
     * Busca anotaciones dentro de un rango de fechas.
     *
     * @param fechaInicial inicio del rango.
     * @param fechaFinal fin del rango.
     * @return anotaciones encontradas.
     */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    );
}
