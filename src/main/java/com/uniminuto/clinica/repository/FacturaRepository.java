package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/** Permite consultar y persistir facturas médicas. */
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    /** Verifica si una cita ya tiene una factura cuyo estado no sea el indicado (ej. distinto de ANULADA). */
    boolean existsByCita_IdAndEstadoNot(Long citaId, String estado);

    /** Lista todas las facturas ordenadas de la más reciente a la más antigua. */
    List<Factura> findAllByOrderByFechaGeneracionDesc();

    /** Consulta el historial de facturación de un propietario (cliente). */
    List<Factura> findByCliente_ClienteIdOrderByFechaGeneracionDesc(Long clienteId);

    /** Consulta el historial de facturación de una mascota. */
    List<Factura> findByMascota_MascotaIdOrderByFechaGeneracionDesc(Integer mascotaId);

    /** Consulta las facturas generadas en un rango de fechas. */
    List<Factura> findByFechaGeneracionBetweenOrderByFechaGeneracionDesc(LocalDateTime inicio, LocalDateTime fin);
}
