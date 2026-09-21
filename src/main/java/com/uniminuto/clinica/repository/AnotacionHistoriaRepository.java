package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de acceso a datos de la entidad AnotacionHistoria.
 *
 * @author Alma Hernandez
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Busca anotaciones dentro de un rango de fechas y las ordena 
     * de la más reciente a la más antigua.
     */
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin);
}