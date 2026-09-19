package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** Implementa las operaciones de medicamentos. */
public class MedicamentoServiceImpl implements MedicamentoService {

    @Autowired
    /** Repositorio de medicamentos. */
    private MedicamentoRepository medicamentoRepository;

    @Override
    // Obtiene todos los medicamentos disponibles.
    public List<Medicamento> obtenerMedicamentos() throws BadRequestException {
        return this.medicamentoRepository.findAll();
    }
}
