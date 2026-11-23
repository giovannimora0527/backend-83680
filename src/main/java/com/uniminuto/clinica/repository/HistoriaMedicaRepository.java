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

    /**
     * Historias de una mascota (antecedentes), de la mas reciente a la mas antigua.
     *
     * @param mascotaId id de la mascota.
     * @return historias.
     */
    List<HistoriaMedica> findByMascotaMascotaIdOrderByFechaHoraDesc(Integer mascotaId);

    /**
     * Historia de una cita.
     *
     * @param citaId id de la cita.
     * @return historias (maximo una).
     */
    List<HistoriaMedica> findByCitaId(Long citaId);
}
