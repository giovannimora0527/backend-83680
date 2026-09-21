package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de las citas.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca las citas entre dos fechas (incluidas), de la mas reciente a la mas antigua.
     *
     * @param inicio fecha y hora inicial del rango.
     * @param fin    fecha y hora final del rango.
     * @return las citas del rango ordenadas.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca las citas que tiene un medico a una fecha y hora exacta.
     *
     * @param medicoId  identificador del medico.
     * @param fechaHora fecha y hora a consultar.
     * @return las citas del medico en ese momento.
     */
    List<Cita> findByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);
}
