package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.util.List;

/** Define las operaciones de negocio para mascotas. */
public interface MascotaService {
    /** Lista todas las mascotas. */

    List<Mascota> listarMascotas() throws BadRequestException;

    List<Mascota> listarMascotasPorCliente(Long clienteId) throws BadRequestException;

    List<Mascota> listarMascotasPorRaza(Integer razaId) throws BadRequestException;

    MiRespuestaRS guardarMascota(MascotaRq mascotaRq) throws BadRequestException;

    MiRespuestaRS actualizarMascota(MascotaRq mascotaRq) throws BadRequestException;

}
    /** Lista las mascotas de un cliente. */
    /** Lista las mascotas de una raza. */
    /** Guarda una mascota nueva. */
    /** Actualiza una mascota existente. */
