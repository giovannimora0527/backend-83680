package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.BloqueoHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Permite consultar y administrar los bloqueos de horario de los veterinarios. */
public interface BloqueoHorarioRepository extends JpaRepository<BloqueoHorario, Long> {

    /** Lista todos los bloqueos activos ordenados por fecha de inicio. */
    List<BloqueoHorario> findByActivoTrueOrderByFechaHoraInicioAsc();

    /** Lista los bloqueos activos de un veterinario ordenados por fecha de inicio. */
    List<BloqueoHorario> findByMedico_IdAndActivoTrueOrderByFechaHoraInicioAsc(Long medicoId);

    /** Busca bloqueos activos de un veterinario que se solapen con el intervalo [inicio, fin]. */
    List<BloqueoHorario> findByMedico_IdAndActivoTrueAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
            Long medicoId, LocalDateTime fin, LocalDateTime inicio);
}
