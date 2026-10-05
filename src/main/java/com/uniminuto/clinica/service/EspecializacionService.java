package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Especializacion;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface EspecializacionService {

    List<Especializacion> listarEspecializaciones() throws BadRequestException;

    Especializacion crearEspecializacion(Especializacion especializacion)
            throws BadRequestException;

    Especializacion actualizarEspecializacion(
            Long id,
            Especializacion especializacion
    ) throws BadRequestException;
}