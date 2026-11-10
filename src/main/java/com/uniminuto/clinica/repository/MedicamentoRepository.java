package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/** Permite consultar y guardar medicamentos del inventario. */
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    /** Verifica si ya existe un medicamento con el nombre indicado. */
    boolean existsByNombreIgnoreCase(String nombre);

    /** Verifica si ya existe un medicamento con el código indicado. */
    boolean existsByCodigoIgnoreCase(String codigo);

    /** Busca un medicamento por su código interno. */
    Optional<Medicamento> findByCodigoIgnoreCase(String codigo);

    /** Obtiene los productos ordenados por fecha de vencimiento para controlar inventario. */
    List<Medicamento> findAllByOrderByFechaVenceAsc();
}
