package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Permite consultar y guardar citas veterinarias. */
public interface CitaRepository extends JpaRepository<Cita, Long> {
    /** Indica si una mascota ya tiene una cita en la misma fecha y hora. */
    boolean existsByMascota_MascotaIdAndFechaHora(Integer mascotaId, LocalDateTime fechaHora);

    /** Busca citas dentro de un rango y las ordena por fecha descendente. */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime fechaInicial, LocalDateTime fechaFinal);
}
