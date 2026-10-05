package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface MedicoService {

    List<Medico> listarMedicos() throws BadRequestException;

    Medico crearMedico(
            Medico medico
    ) throws BadRequestException;

    Medico actualizarMedico(
            Long id,
            Medico medico
    ) throws BadRequestException;
}