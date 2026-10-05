package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.UsuarioService;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    // ==========================================
    // LISTAR USUARIOS
    // ==========================================

    @Override
    public List<Usuario> listarUsuarios()
            throws BadRequestException {

        return usuarioRepository.findAll();
    }

    // ==========================================
    // CREAR USUARIO
    // ==========================================

    @Override
    public Usuario crearUsuario(
            Usuario usuario
    ) throws BadRequestException {

        // Validar username
        if (usuario.getUsername() == null ||
                usuario.getUsername().isBlank()) {

            throw new BadRequestException(
                    "El nombre de usuario es obligatorio"
            );
        }

        // Validar rol
        if (usuario.getRol() == null ||
                usuario.getRol().isBlank()) {

            throw new BadRequestException(
                    "El rol es obligatorio"
            );
        }

        // Validar email
        if (usuario.getEmail() == null ||
                usuario.getEmail().isBlank()) {

            throw new BadRequestException(
                    "El correo electrónico es obligatorio"
            );
        }

        // Validar username duplicado
        if (usuarioRepository.existsByUsername(
                usuario.getUsername()
        )) {

            throw new BadRequestException(
                    "Ya existe un usuario con ese username"
            );
        }

        // Validar email duplicado
        if (usuarioRepository.existsByEmail(
                usuario.getEmail()
        )) {

            throw new BadRequestException(
                    "Ya existe un usuario con ese correo electrónico"
            );
        }

        // Fecha de creación
        if (usuario.getFechaCreacion() == null) {

            usuario.setFechaCreacion(
                    LocalDateTime.now()
            );
        }

        // Estado por defecto
        if (usuario.getActivo() == null) {

            usuario.setActivo(true);
        }

        return usuarioRepository.save(usuario);
    }

    // ==========================================
    // ACTUALIZAR USUARIO
    // ==========================================

    @Override
    public Usuario actualizarUsuario(
            Long id,
            Usuario usuario
    ) throws BadRequestException {

        Usuario usuarioExistente =
                usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "El usuario no existe"
                                )
                        );

        // Validar username
        if (usuario.getUsername() == null ||
                usuario.getUsername().isBlank()) {

            throw new BadRequestException(
                    "El nombre de usuario es obligatorio"
            );
        }

        // Validar rol
        if (usuario.getRol() == null ||
                usuario.getRol().isBlank()) {

            throw new BadRequestException(
                    "El rol es obligatorio"
            );
        }

        // Validar email
        if (usuario.getEmail() == null ||
                usuario.getEmail().isBlank()) {

            throw new BadRequestException(
                    "El correo electrónico es obligatorio"
            );
        }

        // Validar username duplicado
        boolean existeUsername =
                usuarioRepository.existsByUsernameAndIdNot(
                        usuario.getUsername(),
                        id
                );

        if (existeUsername) {

            throw new BadRequestException(
                    "Ya existe otro usuario con ese username"
            );
        }

        // Validar email duplicado
        boolean existeEmail =
                usuarioRepository.existsByEmailAndIdNot(
                        usuario.getEmail(),
                        id
                );

        if (existeEmail) {

            throw new BadRequestException(
                    "Ya existe otro usuario con ese correo electrónico"
            );
        }

        // Actualizar datos
        usuarioExistente.setUsername(
                usuario.getUsername()
        );

        usuarioExistente.setRol(
                usuario.getRol()
        );

        usuarioExistente.setEmail(
                usuario.getEmail()
        );

        // Solo actualizar contraseña
        // si se recibe una nueva
        if (usuario.getPasswordHash() != null &&
                !usuario.getPasswordHash().isBlank()) {

            usuarioExistente.setPasswordHash(
                    usuario.getPasswordHash()
            );
        }

        return usuarioRepository.save(
                usuarioExistente
        );
    }

    // ==========================================
    // CAMBIAR ESTADO
    // ==========================================

    @Override
    public Usuario cambiarEstado(
            Long id,
            Boolean activo
    ) throws BadRequestException {

        Usuario usuario =
                usuarioRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "El usuario no existe"
                                )
                        );

        usuario.setActivo(activo);

        return usuarioRepository.save(usuario);
    }
}