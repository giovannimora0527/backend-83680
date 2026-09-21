package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacion de la logica de los medicamentos.
 */
@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    /**
     * Repositorio de medicamentos.
     */
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    /**
     * Lista los medicamentos del inventario.
     *
     * @return los medicamentos registrados.
     */
    @Override
    public List<Medicamento> obtenerMedicamentos() {
        return this.medicamentoRepository.findAll();
    }
}
