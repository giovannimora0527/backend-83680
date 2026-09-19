package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/** Permite consultar y guardar anotaciones de historias médicas. */
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {
    /** Indica si una historia médica tiene anotaciones asociadas. */
    boolean existsByHistoriaMedica_Id(Long historiaMedicaId);

    /** Busca anotaciones entre dos fechas, ordenadas de forma descendente. */
    @Query("""
            SELECT anotacion
            FROM AnotacionHistoria anotacion
            JOIN FETCH anotacion.historiaMedica historia
            WHERE anotacion.fecha BETWEEN :fechaInicial AND :fechaFinal
            ORDER BY anotacion.fecha DESC
            """)
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(
            @Param("fechaInicial") LocalDateTime fechaInicial,
            @Param("fechaFinal") LocalDateTime fechaFinal);
}
