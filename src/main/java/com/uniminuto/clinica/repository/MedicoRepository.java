package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla medico.
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    /**
     * Lista los medicos ordenados por nombres y apellidos.
     *
     * @return medicos ordenados.
     */
    List<Medico> findAllByOrderByNombresAscApellidosAsc();

    /**
     * Revisa si ya existe un medico con ese documento.
     *
     * @param numeroDocumento numero de documento.
     * @return true si existe.
     */
    boolean existsByNumeroDocumento(String numeroDocumento);

    /**
     * Revisa si ya existe un medico con ese registro profesional.
     *
     * @param registroProfesional registro profesional.
     * @return true si existe.
     */
    boolean existsByRegistroProfesional(String registroProfesional);

    /**
     * Revisa si otro medico (distinto al que se edita) tiene ese documento.
     *
     * @param numeroDocumento numero de documento.
     * @param id              id del medico que se edita.
     * @return true si existe.
     */
    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    /**
     * Revisa si otro medico (distinto al que se edita) tiene ese registro profesional.
     *
     * @param registroProfesional registro profesional.
     * @param id                  id del medico que se edita.
     * @return true si existe.
     */
    boolean existsByRegistroProfesionalAndIdNot(String registroProfesional, Long id);
}
