package com.uniminuto.clinica.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuracion web: CORS, interceptor de seguridad y documentacion Swagger.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Interceptor que valida token y permisos. */
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/swagger-ui/**", "/swagger-ui.html",
                        "/v3/api-docs/**", "/error");
    }

    /**
     * Informacion de Swagger y boton "Authorize" para enviar el token.
     *
     * @return configuracion OpenAPI.
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("API Clínica Veterinaria")
                        .version("1.0")
                        .description("Servicios del examen final: inventario, citas, atención, "
                                + "facturación, despacho y reportes. Primero haga POST /auth/login "
                                + "y pegue el token en el botón Authorize."))
                .addSecurityItem(new SecurityRequirement().addList("token"))
                .components(new Components().addSecuritySchemes("token",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")));
    }
}
