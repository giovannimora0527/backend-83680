package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla historia_medica.
 */
@Repository
public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {

    /**
     * Todas las historias, de la consulta mas reciente a la mas antigua.
     *
     * @return historias ordenadas.
     */
    List<HistoriaMedica> findAllByOrderByFechaHoraDesc();

    /**
     * Revisa si ya existe una historia para una cita (para no duplicar).
     *
     * @param citaId id de la cita.
     * @return true si ya existe.
     */
    boolean existsByCitaId(Long citaId);
}
