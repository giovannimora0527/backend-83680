package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface MascotaService {

    List<Mascota> listarMascotas() throws BadRequestException;

    List<Mascota> listarMascotasPorCliente(Long clienteId) throws BadRequestException;

    List<Mascota> listarMascotasPorRaza(Integer razaId) throws BadRequestException;
}
