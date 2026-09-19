package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime fechaInicial, LocalDateTime fechaFinal);
}
