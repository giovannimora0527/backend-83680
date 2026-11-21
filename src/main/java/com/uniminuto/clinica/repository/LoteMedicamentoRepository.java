package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.LoteMedicamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteMedicamentoRepository extends JpaRepository<LoteMedicamento, Long> {
    List<LoteMedicamento> findByMedicamentoIdOrderByFechaVencimientoAscFechaIngresoAscIdAsc(Long medicamentoId);

    boolean existsByMedicamentoId(Long medicamentoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select lote from LoteMedicamento lote
            where lote.medicamento.id = :medicamentoId and lote.activo = true
            order by lote.fechaVencimiento asc, lote.fechaIngreso asc, lote.id asc
            """)
    List<LoteMedicamento> buscarTodosParaAjuste(@Param("medicamentoId") Long medicamentoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select lote from LoteMedicamento lote
            where lote.medicamento.id = :medicamentoId
              and lote.activo = true
              and lote.cantidadDisponible > 0
              and lote.fechaVencimiento >= :fecha
            order by lote.fechaVencimiento asc, lote.fechaIngreso asc, lote.id asc
            """)
    List<LoteMedicamento> buscarDisponiblesPorVencer(
            @Param("medicamentoId") Long medicamentoId, @Param("fecha") LocalDate fecha);
}
