package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** Implementa las operaciones de médicos. */
public class MedicoServiceImpl implements MedicoService {

    @Autowired
    /** Repositorio de médicos. */
    private MedicoRepository medicoRepository;

    @Override
    // Obtiene la lista completa de médicos registrada en la base de datos.
    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }
}
