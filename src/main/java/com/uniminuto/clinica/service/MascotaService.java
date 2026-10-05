package com.uniminuto.clinica.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.uniminuto.clinica.entity.Mascota;

public interface MascotaService {

    List<Mascota> listarMascotas() throws BadRequestException;

    List<Mascota> listarMascotasPorCliente(Long clienteId) throws BadRequestException;

    List<Mascota> listarMascotasPorRaza(Integer razaId) throws BadRequestException;

    Mascota crearMascota(Mascota mascota) throws BadRequestException;

    Mascota actualizarMascota(Integer mascotaId, Mascota mascota) throws BadRequestException;
}