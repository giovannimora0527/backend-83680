package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define las operaciones CRUD para la entidad Cita.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Query que obtiene las citas organizadas por fecha y hora.
     *
     * @return lista de citas del sistema.
     */
    List<Cita> findAllByOrderByFechaHoraDesc();

    /**
     * Consulta las citas que se encuentran dentro de un rango de fechas
     * y las ordena desde la más reciente hasta la más antigua.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de citas dentro del rango de fechas.
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaIni, LocalDateTime fechaFin);

    /**
     * Consulta que obtiene las citas de un médico en un rango de fechas.
     *
     * @param medicoId médico asociado a las citas.
     * @param fechaIni fecha inicio de consulta.
     * @param fechaFin fecha final de consulta.
     * @return lista de citas asignadas al médico en las fechas.
     */
    List<Cita> findByMedicoAndFechaHoraBetween(
            Medico medicoId, LocalDateTime fechaIni, LocalDateTime fechaFin);
}