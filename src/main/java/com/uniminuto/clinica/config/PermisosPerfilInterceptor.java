package com.uniminuto.clinica.config;

import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.UsuarioSistemaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermisosPerfilInterceptor implements HandlerInterceptor {
    private final JwtTokenService jwtTokenService;
    private final UsuarioSistemaRepository usuarioRepository;

    public PermisosPerfilInterceptor(JwtTokenService jwtTokenService, UsuarioSistemaRepository usuarioRepository) {
        this.jwtTokenService = jwtTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (esRutaPublica(path)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return rechazar(response, HttpStatus.UNAUTHORIZED, "Se requiere un token de acceso válido");
        }

        JwtTokenService.TokenClaims claims;
        try {
            claims = jwtTokenService.validar(authorization.substring(7).trim());
        } catch (BadRequestException e) {
            return rechazar(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        request.setAttribute("authenticatedUserId", claims.userId());
        request.setAttribute("authenticatedUsername", claims.username());
        request.setAttribute("authenticatedRole", claims.role());

        String requestedRole = request.getParameter("rol");
        if (requestedRole != null && !requestedRole.equalsIgnoreCase(claims.role())) {
            return rechazar(response, HttpStatus.FORBIDDEN, "El rol solicitado no coincide con la sesión autenticada");
        }
        if (!tienePermiso(claims.role(), path, request.getMethod())) {
            return rechazar(response, HttpStatus.FORBIDDEN, "Acceso denegado para el perfil " + claims.role());
        }
        if ("CLIENTE".equalsIgnoreCase(claims.role())
                && path.endsWith("/mascota/listar-by-cliente")) {
            Long clienteId = usuarioRepository.findClienteIdById(claims.userId()).orElse(null);
            if (clienteId == null || !clienteId.toString().equals(request.getParameter("clienteId"))) {
                return rechazar(response, HttpStatus.FORBIDDEN, "Solo puede consultar mascotas asociadas a su cuenta");
            }
        }
        return true;
    }

    private boolean esRutaPublica(String path) {
        return path.endsWith("/auth/login")
                || path.contains("/v3/api-docs")
                || path.contains("/swagger-ui")
                || path.endsWith("/actuator/health");
    }

    private boolean tienePermiso(String role, String path, String method) {
        String perfil = role.toUpperCase();
        if ("ADMINISTRADOR".equals(perfil)) return true;

        if ("DESPACHO_MEDICO".equals(perfil)) {
            if (path.contains("/reporte/")) return path.endsWith("/reporte/inventario")
                    || path.endsWith("/reporte/exportar-excel") || path.endsWith("/reporte/exportar-pdf");
            if (path.contains("/medicamento")) return "GET".equals(method);
            if (!path.contains("/formula-medica")) return false;
            if (path.endsWith("/pendientes-despacho")) return "GET".equals(method);
            if (path.endsWith("/consultar")) return "GET".equals(method);
            if (path.matches(".*/[^/]+/(comprobante-pdf|historial)$")) return "GET".equals(method);
            return path.matches(".*/[^/]+/(preparar|despachar)$")
                    && (("preparar".equals(path.substring(path.lastIndexOf('/') + 1)) && "PATCH".equals(method))
                    || ("despachar".equals(path.substring(path.lastIndexOf('/') + 1)) && "POST".equals(method)));
        }
        if ("CLIENTE".equals(perfil)) {
            return path.endsWith("/mascota/listar-by-cliente") && "GET".equals(method);
        }
        if ("VETERINARIO".equals(perfil)) {
            if (path.contains("/usuario") || path.contains("/factura")) return false;
            if (path.contains("/medicamento")) return "GET".equals(method);
            if (path.contains("/formula-medica")) {
                if ("GET".equals(method)) return true;
                return ("POST".equals(method) && path.endsWith("/guardar"))
                        || ("PUT".equals(method) && path.endsWith("/actualizar"))
                        || ("PATCH".equals(method) && path.endsWith("/inactivar"));
            }
            if (path.contains("/reporte/")) return path.endsWith("/reporte/gestion-medica")
                    || path.endsWith("/reporte/exportar-excel") || path.endsWith("/reporte/exportar-pdf");
            if (path.contains("/medicamento") || path.contains("/cliente") || path.contains("/mascota")
                    || path.contains("/raza") || path.contains("/medico") || path.contains("/cita")
                    || path.contains("/formula-medica") || path.contains("/historia-medica")
                    || path.contains("/anotacion-historia")) return true;
            return false;
        }
        if ("SECRETARIA".equals(perfil)) {
            if (path.contains("/usuario") || path.contains("/historia-medica")
                    || path.contains("/anotacion-historia") || path.contains("/formula-medica")) return false;
            if (path.contains("/reporte/")) return path.endsWith("/reporte/financiero")
                    || path.endsWith("/reporte/gestion-medica")
                    || path.endsWith("/reporte/exportar-excel") || path.endsWith("/reporte/exportar-pdf");
            return path.contains("/cliente") || path.contains("/mascota") || path.contains("/medico")
                    || path.contains("/cita") || path.contains("/factura") || path.contains("/raza");
        }
        return false;
    }

    private boolean rechazar(HttpServletResponse response, HttpStatus status, String mensaje) throws Exception {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + status.value() + ",\"message\":\""
                + mensaje.replace("\"", "\\\"") + "\"}");
        return false;
    }
}
