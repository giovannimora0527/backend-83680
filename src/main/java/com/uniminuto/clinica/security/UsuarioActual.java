package com.uniminuto.clinica.security;

import com.uniminuto.clinica.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Ayuda para obtener desde los servicios el usuario que hizo la peticion.
 */
public final class UsuarioActual {

    /** Nombre del atributo donde el interceptor guarda la sesion. */
    public static final String ATRIBUTO = "sesionUsuario";

    private UsuarioActual() {
    }

    /**
     * Devuelve el usuario autenticado de la peticion actual.
     *
     * @return datos del usuario.
     */
    public static SesionUsuario get() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new BadRequestException("Debe iniciar sesión", HttpStatus.UNAUTHORIZED);
        }
        HttpServletRequest request = attrs.getRequest();
        SesionUsuario sesion = (SesionUsuario) request.getAttribute(ATRIBUTO);
        if (sesion == null) {
            throw new BadRequestException("Debe iniciar sesión", HttpStatus.UNAUTHORIZED);
        }
        return sesion;
    }

    /**
     * Devuelve el id del medico del usuario actual (error si no es medico).
     *
     * @return id del medico.
     */
    public static Long medicoId() {
        Long medicoId = get().getMedicoId();
        if (medicoId == null) {
            throw new BadRequestException("Su usuario no está asociado a un médico", HttpStatus.FORBIDDEN);
        }
        return medicoId;
    }
}
