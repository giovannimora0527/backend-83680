package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Usuario;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UsuarioService {

    List<Usuario> listarUsuarios()
            throws BadRequestException;

    Usuario crearUsuario(Usuario usuario)
            throws BadRequestException;

    Usuario actualizarUsuario(
            Long id,
            Usuario usuario
    ) throws BadRequestException;

    Usuario cambiarEstado(
            Long id,
            Boolean activo
    ) throws BadRequestException;
}