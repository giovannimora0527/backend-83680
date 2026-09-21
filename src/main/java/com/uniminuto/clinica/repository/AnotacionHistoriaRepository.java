package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de las anotaciones de historia medica.
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Busca las anotaciones entre dos fechas (incluidas), de la mas reciente a la mas antigua.
     *
     * @param inicio fecha y hora inicial del rango.
     * @param fin    fecha y hora final del rango.
     * @return las anotaciones del rango ordenadas.
     */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);
}
