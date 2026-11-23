package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.MedicamentoLote;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de la tabla medicamento_lote.
 */
@Repository
public interface MedicamentoLoteRepository extends JpaRepository<MedicamentoLote, Long> {

    /**
     * Lotes de un medicamento, del que vence primero al ultimo.
     *
     * @param medicamentoId id del medicamento.
     * @return lotes.
     */
    @Query("SELECT l FROM MedicamentoLote l WHERE l.medicamento.id = :medicamentoId "
            + "ORDER BY CASE WHEN l.fechaVence IS NULL THEN 1 ELSE 0 END, l.fechaVence, l.id")
    List<MedicamentoLote> findLotesDeMedicamento(Long medicamentoId);

    /**
     * Lotes vigentes con existencias, ordenados por vencimiento (FEFO: primero el que vence antes).
     *
     * @param medicamentoId id del medicamento.
     * @param hoy           fecha actual.
     * @return lotes que se pueden dispensar.
     */
    @Query("SELECT l FROM MedicamentoLote l WHERE l.medicamento.id = :medicamentoId AND l.cantidadDisponible > 0 "
            + "AND (l.fechaVence IS NULL OR l.fechaVence >= :hoy) "
            + "ORDER BY CASE WHEN l.fechaVence IS NULL THEN 1 ELSE 0 END, l.fechaVence, l.id")
    List<MedicamentoLote> findLotesVigentes(Long medicamentoId, LocalDate hoy);

    /**
     * Revisa si ya existe un lote con ese numero para el medicamento.
     *
     * @param medicamentoId id del medicamento.
     * @param numeroLote    numero del lote.
     * @return true si existe.
     */
    boolean existsByMedicamentoIdAndNumeroLoteIgnoreCase(Long medicamentoId, String numeroLote);

    /**
     * Busca un lote bloqueandolo hasta que termine la transaccion (evita que dos
     * despachos descuenten el mismo lote al mismo tiempo).
     *
     * @param id id del lote.
     * @return el lote bloqueado.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM MedicamentoLote l WHERE l.id = :id")
    java.util.Optional<MedicamentoLote> findBloqueado(Long id);
}
