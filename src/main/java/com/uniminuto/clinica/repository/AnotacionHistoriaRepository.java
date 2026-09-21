package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/** Permite consultar y guardar anotaciones de historias médicas. */
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {
    /** Indica si ya existe la misma anotación en una historia médica. */
    boolean existsByHistoriaMedica_IdAndMedico_IdAndDescripcion(
            Long historiaMedicaId, Long medicoId, String descripcion);

    /** Indica si una historia médica tiene anotaciones asociadas. */
    boolean existsByHistoriaMedica_Id(Long historiaMedicaId);

    /** Busca anotaciones entre dos fechas, ordenadas de forma descendente. */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(
            @Param("fechaInicial") LocalDateTime fechaInicial,
            @Param("fechaFinal") LocalDateTime fechaFinal);
}
