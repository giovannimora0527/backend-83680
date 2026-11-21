package com.uniminuto.clinica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI clinicaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API Clínica Veterinaria")
                .version("1.0")
                .description("API para administración clínica, inventario, fórmulas médicas y dispensación."));
    }
}
