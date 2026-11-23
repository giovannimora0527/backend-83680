package com.uniminuto.clinica.security;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Revisa en cada peticion que venga un token valido (Authorization: Bearer ...)
 * y que el perfil del usuario tenga permiso sobre la ruta.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** Servicio que valida el token contra la tabla session. */
    @Autowired
    private AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Las peticiones OPTIONS son del navegador (CORS), no traen token
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // Paso 1. Leer el token
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ") || header.length() <= 7) {
            throw new BadRequestException("Debe iniciar sesión para usar el sistema", HttpStatus.UNAUTHORIZED);
        }
        // Paso 2. Validar la sesion (lanza 401 si no existe o vencio)
        SesionUsuario sesion = authService.validarToken(header.substring(7).trim());

        // Paso 3. Validar el permiso del perfil sobre la ruta
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        if (!Permisos.tienePermiso(ruta, request.getMethod().toUpperCase(), sesion.getPerfilId())) {
            throw new BadRequestException("Su perfil no tiene permiso para esta opción", HttpStatus.FORBIDDEN);
        }
        request.setAttribute(UsuarioActual.ATRIBUTO, sesion);
        return true;
    }
}
