package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface MascotaService {

    List<Mascota> listarMascotas() throws BadRequestException;

    List<Mascota> listarMascotasPorCliente(Long clienteId) throws BadRequestException;

    List<Mascota> listarMascotasPorRaza(Integer razaId) throws BadRequestException;

    MiRespuestaRS guardarMascota(MascotaRq mascotaRq) throws BadRequestException;

    MiRespuestaRS actualizarMascota(MascotaRq mascotaRq) throws BadRequestException;

}
