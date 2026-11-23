package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.security.SesionUsuario;

/**
 * Servicio de inicio y cierre de sesion.
 */
public interface AuthService {

    /**
     * Valida usuario y contrasena y abre una sesion.
     *
     * @param loginRq credenciales.
     * @return token, datos del usuario y menus.
     */
    LoginRs login(LoginRq loginRq);

    /**
     * Valida un token y devuelve el usuario de la sesion (401 si no es valido).
     *
     * @param token token del header.
     * @return datos del usuario.
     */
    SesionUsuario validarToken(String token);

    /**
     * Devuelve los datos de la sesion actual (para refrescar menus en el front).
     *
     * @return datos del usuario y menus.
     */
    LoginRs sesionActual();

    /**
     * Cierra la sesion actual.
     *
     * @param token token del header.
     * @return respuesta de exito.
     */
    MiRespuestaRS logout(String token);

    /**
     * Calcula el MD5 de un texto (asi guarda las contrasenas el script del docente).
     *
     * @param texto texto plano.
     * @return hash en hexadecimal.
     */
    String md5(String texto);
}
