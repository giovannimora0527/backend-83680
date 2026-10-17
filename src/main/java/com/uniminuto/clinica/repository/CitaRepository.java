package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link Cita}.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Cita> findByMascotaAndMedico(Mascota mascota, Medico medico);

    List<Cita> findByMedicoAndFechaHoraBetween(Medico medico, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Indica si el médico tiene otra cita activa (no cancelada) dentro del rango.
     *
     * @param medico    médico a consultar
     * @param desde     inicio del rango
     * @param hasta     fin del rango
     * @param excluirId id de la cita a ignorar (al editar), o null
     * @return true si existe un choque de horario
     */
    @Query("""
        SELECT COUNT(c) > 0 FROM Cita c
        WHERE c.medico = :medico AND c.estado <> 'CANCELADA'
          AND c.fechaHora BETWEEN :desde AND :hasta
          AND (:excluirId IS NULL OR c.id <> :excluirId)
    """)
    boolean existeCitaMedicoEnRango(@Param("medico") Medico medico,
                                    @Param("desde") LocalDateTime desde,
                                    @Param("hasta") LocalDateTime hasta,
                                    @Param("excluirId") Long excluirId);

    /**
     * Indica si la mascota tiene otra cita activa (no cancelada) dentro del rango.
     *
     * @param mascota   mascota a consultar
     * @param desde     inicio del rango
     * @param hasta     fin del rango
     * @param excluirId id de la cita a ignorar (al editar), o null
     * @return true si existe un choque de horario
     */
    @Query("""
        SELECT COUNT(c) > 0 FROM Cita c
        WHERE c.mascota = :mascota AND c.estado <> 'CANCELADA'
          AND c.fechaHora BETWEEN :desde AND :hasta
          AND (:excluirId IS NULL OR c.id <> :excluirId)
    """)
    boolean existeCitaMascotaEnRango(@Param("mascota") Mascota mascota,
                                     @Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta,
                                     @Param("excluirId") Long excluirId);

    /**
     * Busca citas con filtros opcionales, paginadas.
     *
     * @param estado   estado de la cita, o null
     * @param medicoId id del médico, o null
     * @param desde    fecha mínima, o null
     * @param hasta    fecha máxima, o null
     * @param pageable paginación y orden
     * @return página de citas
     */
    @Query("""
        SELECT c FROM Cita c
        WHERE (:estado IS NULL OR c.estado = :estado)
          AND (:medicoId IS NULL OR c.medico.id = :medicoId)
          AND (:desde IS NULL OR c.fechaHora >= :desde)
          AND (:hasta IS NULL OR c.fechaHora <= :hasta)
    """)
    Page<Cita> buscar(@Param("estado") String estado,
                      @Param("medicoId") Long medicoId,
                      @Param("desde") LocalDateTime desde,
                      @Param("hasta") LocalDateTime hasta,
                      Pageable pageable);
}