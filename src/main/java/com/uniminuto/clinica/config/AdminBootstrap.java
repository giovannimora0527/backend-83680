package com.uniminuto.clinica.config;

import com.uniminuto.clinica.entity.UsuarioSistema;
import com.uniminuto.clinica.repository.UsuarioSistemaRepository;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UsuarioSistemaRepository usuarioRepository;
    private final String username;
    private final String password;
    private final PasswordHasher passwordHasher;

    public AdminBootstrap(UsuarioSistemaRepository usuarioRepository,
                          PasswordHasher passwordHasher,
                          @Value("${clinica.bootstrap-admin.username:}") String username,
                          @Value("${clinica.bootstrap-admin.password:}") String password) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username.isBlank() && password.isBlank()) return;
        if (username.isBlank() || password.length() < 12) {
            throw new IllegalStateException("Configure ADMIN_USERNAME y ADMIN_PASSWORD (mínimo 12 caracteres)");
        }
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) return;
        UsuarioSistema admin = new UsuarioSistema();
        admin.setUsername(username.trim());
        admin.setPasswordHash(passwordHasher.hash(password));
        admin.setRol(Constantes.ROL_ADMINISTRADOR);
        admin.setActivo(true);
        admin.setFechaCreacion(LocalDateTime.now());
        usuarioRepository.save(admin);
    }
}
