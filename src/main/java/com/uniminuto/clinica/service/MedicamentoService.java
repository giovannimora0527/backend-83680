package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import org.apache.coyote.BadRequestException;

import java.util.List;

/** Define las operaciones de negocio para medicamentos. */
public interface MedicamentoService {
    /** Lista los medicamentos disponibles. */

    List<Medicamento> obtenerMedicamentos() throws BadRequestException;
}
