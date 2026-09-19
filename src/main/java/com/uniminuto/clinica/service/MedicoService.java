package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;

import java.util.List;

/** Define las operaciones de negocio para médicos. */
public interface MedicoService {
    /** Lista los médicos registrados. */

    List<Medico> listarMedicos();
}
