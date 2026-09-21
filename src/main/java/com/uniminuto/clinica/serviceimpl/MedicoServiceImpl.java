package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacion de la logica de los medicos.
 */
@Service
public class MedicoServiceImpl implements MedicoService {

    /**
     * Repositorio de medicos.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Lista los medicos de la clinica.
     *
     * @return los medicos registrados.
     */
    @Override
    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }
}
