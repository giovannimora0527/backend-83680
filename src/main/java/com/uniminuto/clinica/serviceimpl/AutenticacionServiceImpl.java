package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.config.JwtTokenService;
import com.uniminuto.clinica.config.PasswordHasher;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.UsuarioSistema;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.exception.UnauthorizedException;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.UsuarioSistemaRepository;
import com.uniminuto.clinica.service.AutenticacionService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.List;
import java.util.Set;

@Service
public class AutenticacionServiceImpl implements AutenticacionService {
    private static final Set<String> ROLES = Set.of(
            Constantes.ROL_ADMINISTRADOR, Constantes.ROL_SECRETARIA,
            Constantes.ROL_VETERINARIO, Constantes.ROL_DESPACHO_MEDICO,
            Constantes.ROL_CLIENTE);

    private final UsuarioSistemaRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final MedicoRepository medicoRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordHasher passwordHasher;

    public AutenticacionServiceImpl(UsuarioSistemaRepository usuarioRepository,
                                    ClienteRepository clienteRepository,
                                    MedicoRepository medicoRepository,
                                    JwtTokenService jwtTokenService,
                                    PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.medicoRepository = medicoRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginRS iniciarSesion(LoginRq solicitud) {
        if (solicitud == null || solicitud.getUsername() == null || solicitud.getPassword() == null) {
            throw new BadRequestException("Usuario y contraseña son obligatorios");
        }
        UsuarioSistema usuario = usuarioRepository.findByUsernameIgnoreCase(solicitud.getUsername().trim())
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        if (!passwordHasher.matches(solicitud.getPassword(), usuario.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        JwtTokenService.TokenClaims claims = jwtTokenService.emitir(
                usuario.getId(), usuario.getUsername(), usuario.getRol());
        return new LoginRS(claims.token(), claims.expiresAt(), toResponse(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRS> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public UsuarioRS crearUsuario(UsuarioRq solicitud) {
        validar(solicitud, true);
        String username = solicitud.getUsername().trim();
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("El nombre de usuario ya está registrado");
        }
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordHasher.hash(solicitud.getPassword()));
        asignarDatos(usuario, solicitud);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioRS actualizarUsuario(Long id, UsuarioRq solicitud) {
        if (id == null || id <= 0) throw new BadRequestException("El ID del usuario no es válido");
        validar(solicitud, false);
        UsuarioSistema usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        usuarioRepository.findByUsernameIgnoreCase(solicitud.getUsername().trim())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("El nombre de usuario ya está registrado"); });
        usuario.setUsername(solicitud.getUsername().trim());
        if (solicitud.getPassword() != null && !solicitud.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordHasher.hash(solicitud.getPassword()));
        }
        asignarDatos(usuario, solicitud);
        return toResponse(usuarioRepository.save(usuario));
    }

    private void asignarDatos(UsuarioSistema usuario, UsuarioRq solicitud) {
        String rol = solicitud.getRol().trim().toUpperCase();
        usuario.setRol(rol);
        usuario.setActivo(solicitud.getActivo() == null || solicitud.getActivo());
        usuario.setCliente(solicitud.getClienteId() == null ? null : clienteRepository.findById(solicitud.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente asociado no encontrado")));
        usuario.setMedico(solicitud.getMedicoId() == null ? null : medicoRepository.findById(solicitud.getMedicoId())
                .orElseThrow(() -> new NotFoundException("Médico asociado no encontrado")));
        if (Constantes.ROL_VETERINARIO.equals(rol) && usuario.getMedico() == null) {
            throw new BadRequestException("El perfil VETERINARIO debe asociarse a un médico");
        }
        if (!Constantes.ROL_VETERINARIO.equals(rol) && solicitud.getMedicoId() != null) {
            throw new BadRequestException("Solo el perfil VETERINARIO puede asociarse a un médico");
        }
        if (Constantes.ROL_CLIENTE.equals(rol) && usuario.getCliente() == null) {
            throw new BadRequestException("El perfil CLIENTE debe asociarse a un propietario");
        }
        if (Constantes.ROL_CLIENTE.equals(rol) && !Boolean.TRUE.equals(usuario.getCliente().getActivo())) {
            throw new BadRequestException("El propietario asociado debe estar activo");
        }
        if (!Constantes.ROL_CLIENTE.equals(rol) && solicitud.getClienteId() != null) {
            throw new BadRequestException("Solo el perfil CLIENTE puede asociarse a un propietario");
        }
    }

    private void validar(UsuarioRq solicitud, boolean requierePassword) {
        if (solicitud == null || solicitud.getUsername() == null || solicitud.getUsername().isBlank()
                || solicitud.getUsername().trim().length() > 80 || solicitud.getRol() == null
                || !ROLES.contains(solicitud.getRol().trim().toUpperCase())) {
            throw new BadRequestException("Usuario válido y rol admitido son obligatorios");
        }
        if (requierePassword && (solicitud.getPassword() == null || solicitud.getPassword().length() < 12)) {
            throw new BadRequestException("La contraseña debe tener al menos 12 caracteres");
        }
        if (solicitud.getPassword() != null && !solicitud.getPassword().isBlank()
                && solicitud.getPassword().length() < 12) {
            throw new BadRequestException("La contraseña debe tener al menos 12 caracteres");
        }
        String role = solicitud.getRol().trim().toUpperCase();
        if (Constantes.ROL_VETERINARIO.equals(role) && solicitud.getMedicoId() == null) {
            throw new BadRequestException("Debe indicar el médico asociado al perfil VETERINARIO");
        }
        if (Constantes.ROL_CLIENTE.equals(role) && solicitud.getClienteId() == null) {
            throw new BadRequestException("Debe indicar el propietario asociado al perfil CLIENTE");
        }
    }

    private UsuarioRS toResponse(UsuarioSistema usuario) {
        return new UsuarioRS(usuario.getId(), usuario.getUsername(), usuario.getRol(), usuario.getActivo(),
                usuario.getCliente() == null ? null : usuario.getCliente().getClienteId(),
                usuario.getMedico() == null ? null : usuario.getMedico().getId());
    }
}
