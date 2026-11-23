package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla horario_medico.
 */
@Repository
public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {

    /**
     * Horarios de un medico ordenados por dia.
     *
     * @param medicoId id del medico.
     * @return horarios.
     */
    List<HorarioMedico> findByMedicoIdOrderByDiaSemanaAscHoraInicioAsc(Long medicoId);

    /**
     * Horarios activos de un medico para un dia.
     *
     * @param medicoId   id del medico.
     * @param diaSemana  1 = lunes ... 7 = domingo.
     * @return horarios del dia.
     */
    List<HorarioMedico> findByMedicoIdAndDiaSemanaAndActivoTrue(Long medicoId, Integer diaSemana);
}
