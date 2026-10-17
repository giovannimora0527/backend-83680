package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HistoriaMedica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repositorio de acceso a datos para la entidad {@link HistoriaMedica}.
 */
@Repository
public interface HistoriaMedicaRepository extends JpaRepository<HistoriaMedica, Long> {

    /**
     * Indica si una cita ya tiene una historia clínica asociada.
     *
     * @param cita cita a consultar.
     * @return true si ya existe una historia para esa cita.
     */
    boolean existsByCita(Cita cita);

    /**
     * Busca historias clínicas con filtros opcionales, paginadas.
     *
     * @param estado    estado de la historia, o null.
     * @param clienteId id del cliente, o null.
     * @param mascotaId id de la mascota, o null.
     * @param medicoId  id del veterinario, o null.
     * @param desde     fecha mínima, o null.
     * @param hasta     fecha máxima, o null.
     * @param texto     texto libre (mascota, cliente, veterinario o motivo); vacío si no aplica.
     * @param pageable  paginación y orden.
     * @return página de historias clínicas.
     */
    @Query("""
        SELECT h FROM HistoriaMedica h
        LEFT JOIN h.mascota m
        LEFT JOIN h.cliente c
        LEFT JOIN h.medico md
        WHERE (:estado IS NULL OR h.estado = :estado)
          AND (:clienteId IS NULL OR c.id = :clienteId)
          AND (:mascotaId IS NULL OR m.mascotaId = :mascotaId)
          AND (:medicoId IS NULL OR md.id = :medicoId)
          AND (:desde IS NULL OR h.fechaHora >= :desde)
          AND (:hasta IS NULL OR h.fechaHora <= :hasta)
          AND (:texto = ''
               OR LOWER(m.nombreMascota) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(CONCAT(c.nombres, ' ', c.apellidos)) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(CONCAT(md.nombres, ' ', md.apellidos)) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(h.motivo) LIKE LOWER(CONCAT('%', :texto, '%')))
    """)
    Page<HistoriaMedica> buscar(@Param("estado") String estado,
                                @Param("clienteId") Long clienteId,
                                @Param("mascotaId") Integer mascotaId,
                                @Param("medicoId") Long medicoId,
                                @Param("desde") LocalDateTime desde,
                                @Param("hasta") LocalDateTime hasta,
                                @Param("texto") String texto,
                                Pageable pageable);
}