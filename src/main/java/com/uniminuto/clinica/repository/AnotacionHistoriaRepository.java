package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {
    List<AnotacionHistoria> findByFechaBetweenOrderByFechaDesc(LocalDateTime fechaInicial, LocalDateTime fechaFinal);
}
