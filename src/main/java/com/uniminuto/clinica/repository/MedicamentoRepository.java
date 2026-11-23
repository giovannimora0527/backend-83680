package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la tabla medicamento.
 */
@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    /**
     * Busca por codigo interno.
     *
     * @param codigo codigo de 8 caracteres.
     * @return el medicamento si existe.
     */
    Optional<Medicamento> findByCodigo(String codigo);

    /**
     * Busca por nombre sin importar mayusculas.
     *
     * @param nombre nombre.
     * @return el medicamento si existe.
     */
    Optional<Medicamento> findByNombreIgnoreCase(String nombre);

    /**
     * Lista ordenada por nombre.
     *
     * @return medicamentos.
     */
    List<Medicamento> findAllByOrderByNombreAsc();
}
