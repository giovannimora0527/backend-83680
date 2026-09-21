package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio con la logica de las mascotas.
 */
public interface MascotaService {

    /**
     * Lista todas las mascotas ordenadas por nombre.
     *
     * @return las mascotas registradas.
     */
    List<Mascota> listarMascotas();

    /**
     * Lista las mascotas de un cliente.
     *
     * @param clienteId identificador del cliente.
     * @return las mascotas del cliente.
     */
    List<Mascota> listarMascotasPorCliente(Long clienteId);

    /**
     * Lista las mascotas de una raza.
     *
     * @param razaId identificador de la raza.
     * @return las mascotas de la raza.
     */
    List<Mascota> listarMascotasPorRaza(Integer razaId);

    /**
     * Guarda una mascota nueva.
     *
     * @param mascotaRq datos de la mascota.
     * @return respuesta de exito.
     */
    MiRespuestaRS guardarMascota(MascotaRq mascotaRq);

    /**
     * Actualiza una mascota que ya existe.
     *
     * @param mascotaRq datos de la mascota, incluido su id.
     * @return respuesta de exito.
     */
    MiRespuestaRS actualizarMascota(MascotaRq mascotaRq);

}
