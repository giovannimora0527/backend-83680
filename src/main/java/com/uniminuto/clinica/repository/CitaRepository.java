package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Permite consultar y guardar citas veterinarias. */
public interface CitaRepository extends JpaRepository<Cita, Long> {
    /** Busca citas dentro de un rango y las ordena por fecha descendente. */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime fechaInicial, LocalDateTime fechaFinal);

    /** Lista todas las citas desde la más reciente a la más antigua. */
    List<Cita> findAllByOrderByFechaHoraDesc();

    /** Busca las citas de un veterinario dentro de un rango de fecha y hora. */
    List<Cita> findByMedico_IdAndFechaHoraBetween(Long medicoId, LocalDateTime fechaInicial,
                                                  LocalDateTime fechaFinal);

    /** Busca las citas de una mascota dentro de un rango de fecha y hora. */
    List<Cita> findByMascota_MascotaIdAndFechaHoraBetween(Integer mascotaId, LocalDateTime fechaInicial,
                                                          LocalDateTime fechaFinal);

    /** Lista las citas de una mascota desde la más reciente a la más antigua. */
    List<Cita> findByMascota_MascotaIdOrderByFechaHoraDesc(Integer mascotaId);
}
