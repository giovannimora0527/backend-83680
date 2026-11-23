package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.DespachoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de la tabla despacho_detalle.
 */
@Repository
public interface DespachoDetalleRepository extends JpaRepository<DespachoDetalle, Long> {
}
