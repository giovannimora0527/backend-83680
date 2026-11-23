package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRs;

import java.util.List;

/**
 * Administracion de usuarios y perfiles.
 */
public interface UsuarioService {

    /**
     * Lista los usuarios.
     *
     * @return usuarios sin contrasena.
     */
    List<UsuarioRs> listarUsuarios();

    /**
     * Lista los perfiles activos.
     *
     * @return perfiles.
     */
    List<Perfil> listarPerfiles();

    /**
     * Crea un usuario.
     *
     * @param usuarioRq datos.
     * @return respuesta de exito.
     */
    MiRespuestaRS guardarUsuario(UsuarioRq usuarioRq);

    /**
     * Edita un usuario.
     *
     * @param usuarioRq datos con id.
     * @return respuesta de exito.
     */
    MiRespuestaRS actualizarUsuario(UsuarioRq usuarioRq);
}
