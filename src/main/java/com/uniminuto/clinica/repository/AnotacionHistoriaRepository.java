package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interfaz que define las operaciones de acceso a datos
 * para la entidad AnotacionHistoria.
 */
@Repository
public interface AnotacionHistoriaRepository
        extends JpaRepository<AnotacionHistoria, Long> {
}