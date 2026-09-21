package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;

import java.util.List;

/**
 * Servicio con la logica de los medicamentos.
 */
public interface MedicamentoService {

    /**
     * Lista los medicamentos del inventario.
     *
     * @return los medicamentos registrados.
     */
    List<Medicamento> obtenerMedicamentos();
}
