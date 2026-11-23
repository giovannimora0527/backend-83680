package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de la tabla cita.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Citas entre dos fechas, de la mas reciente a la mas antigua.
     *
     * @param inicio fecha y hora inicial.
     * @param fin    fecha y hora final.
     * @return citas del rango.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Todas las citas, de la mas reciente a la mas antigua.
     *
     * @return todas las citas.
     */
    List<Cita> findAllByOrderByFechaHoraDesc();

    /**
     * Citas activas de un medico que se cruzan con el horario [inicio, fin).
     *
     * @param medicoId id del medico.
     * @param inicio   inicio de la cita nueva.
     * @param fin      fin de la cita nueva.
     * @return citas que se superponen.
     */
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId AND c.estado <> 'cancelada' "
            + "AND c.fechaHora < :fin AND c.fechaHoraFin > :inicio")
    List<Cita> findCrucesMedico(Long medicoId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Citas activas de una mascota que se cruzan con el horario [inicio, fin).
     *
     * @param mascotaId id de la mascota.
     * @param inicio    inicio de la cita nueva.
     * @param fin       fin de la cita nueva.
     * @return citas que se superponen.
     */
    @Query("SELECT c FROM Cita c WHERE c.mascota.mascotaId = :mascotaId AND c.estado <> 'cancelada' "
            + "AND c.fechaHora < :fin AND c.fechaHoraFin > :inicio")
    List<Cita> findCrucesMascota(Integer mascotaId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Citas de un medico en un rango (para el calendario y el reporte).
     *
     * @param medicoId id del medico.
     * @param inicio   desde.
     * @param fin      hasta.
     * @return citas ordenadas por fecha.
     */
    List<Cita> findByMedicoIdAndFechaHoraBetweenOrderByFechaHoraAsc(Long medicoId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Citas de una mascota, de la mas reciente a la mas antigua.
     *
     * @param mascotaId id de la mascota.
     * @return citas de la mascota.
     */
    List<Cita> findByMascotaMascotaIdOrderByFechaHoraDesc(Integer mascotaId);
}
