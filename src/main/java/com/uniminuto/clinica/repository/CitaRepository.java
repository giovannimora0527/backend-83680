package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * Citas de un medico dentro de un rango de tiempo (para validar choques de horario).
     *
     * @param medicoId id del medico.
     * @param inicio   inicio del rango.
     * @param fin      fin del rango.
     * @return citas del medico en ese rango.
     */
    List<Cita> findByMedicoIdAndFechaHoraBetween(Long medicoId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Citas de una mascota dentro de un rango de tiempo (para validar citas simultaneas).
     *
     * @param mascotaId id de la mascota.
     * @param inicio    inicio del rango.
     * @param fin       fin del rango.
     * @return citas de la mascota en ese rango.
     */
    List<Cita> findByMascotaMascotaIdAndFechaHoraBetween(Integer mascotaId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Citas de una mascota, de la mas reciente a la mas antigua.
     *
     * @param mascotaId id de la mascota.
     * @return citas de la mascota.
     */
    List<Cita> findByMascotaMascotaIdOrderByFechaHoraDesc(Integer mascotaId);
}
