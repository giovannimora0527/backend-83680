package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.FormulaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repositorio de la tabla formula_medica.
 */
@Repository
public interface FormulaMedicaRepository extends JpaRepository<FormulaMedica, Long> {

    /** @return formulas de la mas reciente a la mas antigua. */
    List<FormulaMedica> findAllByOrderByFechaCreacionRegistroDesc();

    /** @return formulas activas de una cita. */
    List<FormulaMedica> findByCitaIdAndActivoTrue(Long citaId);

    /** @return formulas activas en ciertos estados de despacho. */
    List<FormulaMedica> findByActivoTrueAndEstadoDespachoInOrderByFechaCreacionRegistroAsc(Collection<String> estados);

    /** @return formulas de una mascota (por su cita). */
    List<FormulaMedica> findByCitaMascotaMascotaIdOrderByFechaCreacionRegistroDesc(Integer mascotaId);
}
