package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad AnotacionHistoria.
 * Proporciona métodos de acceso a datos y consultas personalizadas para las anotaciones clínicas[cite: 15].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Filtra anotaciones entre dos fechas ordenadas de la mas reciente a la mas antigua[cite: 15].
     *
     * @param fechaInicio Fecha inicial del rango de consulta[cite: 15]
     * @param fechaFin Fecha final del rango de consulta[cite: 15]
     * @return Lista de anotaciones dentro del rango especificado, ordenadas descendentemente[cite: 15]
     */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}