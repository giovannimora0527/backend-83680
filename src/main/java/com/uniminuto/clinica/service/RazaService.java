package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Raza;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface RazaService {

    List<Raza> listarRazas() throws BadRequestException;

    Raza crearRaza(Raza raza) throws BadRequestException;

    Raza actualizarRaza(Integer razaId, Raza raza) throws BadRequestException;
}