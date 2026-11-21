package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRS;

import java.util.List;

public interface AutenticacionService {
    LoginRS iniciarSesion(LoginRq solicitud);
    List<UsuarioRS> listarUsuarios();
    UsuarioRS crearUsuario(UsuarioRq solicitud);
    UsuarioRS actualizarUsuario(Long id, UsuarioRq solicitud);
}
