package com.uniminuto.clinica.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.repository.UsuarioRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Clase de configuracion para la seguridad.
 *
 * @author lmora
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {
            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            if (!usuario.isActivo()) {
                throw new UsernameNotFoundException("Usuario inactivo");
            }
            String rol = usuario.getRol() == null ? "USUARIO" : usuario.getRol().toUpperCase();
            return User.withUsername(usuario.getUsername())
                    .password(usuario.getPassword())
                    .roles(rol)
                    .build();
        };
    }

   /**
     * Filtro de seguridad.
     *
     * @param http peticion de entrada.
     * @return Autorizado.
     * @throws Exception Excepcion.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .cors() // Habilita CORS
                .and()
                .csrf().disable()
                .httpBasic()
                .and()
                .authorizeHttpRequests((requests) -> requests
                .antMatchers("/clinica/test","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.GET, "/usuario/**").permitAll()
                .antMatchers(HttpMethod.POST, "/usuario/guardar").hasRole("Administrador")
                .antMatchers("/despacho/**").hasRole("DESPACHO_MEDICO")
                .anyRequest().authenticated()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    /**
     * Configuracion del cors.
     *
     * @return configuracion.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:8080",
                "http://127.0.0.1:8080",
                "http://127.0.0.1:4200",
                "http://10.0.5.50:8080",
                "http://10.0.5.50:4200"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*", "Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
