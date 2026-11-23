package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio de las especializaciones.
 */
public interface EspecializacionService {

    /** @return especialidades ordenadas por nombre. */
    List<Especializacion> listarEspecializaciones();

    /** @return respuesta al registrar. */
    MiRespuestaRS guardarEspecializacion(Especializacion especializacion);

    /** @return respuesta al editar. */
    MiRespuestaRS actualizarEspecializacion(Especializacion especializacion);

    /** @return respuesta al activar o inactivar. */
    MiRespuestaRS cambiarEstado(Long id, Boolean activo);
}
