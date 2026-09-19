package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** Implementa las operaciones de fórmulas médicas. */
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    /** Repositorio de fórmulas médicas. */
    private FormulaMedicaRepository formulaMedicaRepository;

    /** Recibe el repositorio utilizado por el servicio. */
    public FormulaMedicaServiceImpl(FormulaMedicaRepository formulaMedicaRepository) {
        this.formulaMedicaRepository = formulaMedicaRepository;
    }

    @Override
    // Consulta todas las fórmulas y las ordena por fecha de creación.
    public List<FormulaMedica> listarFormulasMedicas() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }
}
