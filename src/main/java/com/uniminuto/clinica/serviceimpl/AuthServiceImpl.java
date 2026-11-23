package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.Sesion;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.LoginRq;
import com.uniminuto.clinica.models.LoginRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.MenuRepository;
import com.uniminuto.clinica.repository.PerfilRepository;
import com.uniminuto.clinica.repository.SesionRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.SesionUsuario;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementacion del inicio de sesion con token guardado en la tabla session.
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** Horas que dura una sesion. */
    private static final int HORAS_SESION = 8;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private SesionRepository sesionRepository;
    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public LoginRs login(LoginRq loginRq) {
        // Paso 1. Validar datos
        if (loginRq == null || loginRq.getUsername() == null || loginRq.getUsername().isBlank()
                || loginRq.getPassword() == null || loginRq.getPassword().isBlank()) {
            throw new BadRequestException("Ingrese el usuario y la contraseña");
        }
        // Paso 2. Buscar el usuario y comparar la contrasena (mismo mensaje para no dar pistas)
        Optional<Usuario> optUsuario = usuarioRepository.findByUsername(loginRq.getUsername().trim());
        if (optUsuario.isEmpty() || !this.md5(loginRq.getPassword()).equalsIgnoreCase(optUsuario.get().getPasswordHash())) {
            throw new BadRequestException("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
        }
        Usuario usuario = optUsuario.get();
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("El usuario está inactivo", HttpStatus.UNAUTHORIZED);
        }
        // Paso 3. Crear la sesion
        Sesion sesion = new Sesion();
        sesion.setUserId(usuario.getId().intValue());
        sesion.setToken(UUID.randomUUID() + "-" + UUID.randomUUID());
        sesion.setFechaIniSesion(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(HORAS_SESION));
        sesionRepository.save(sesion);

        LoginRs rs = this.armarRespuesta(usuario);
        rs.setToken(sesion.getToken());
        rs.setFechaExpiracion(sesion.getFechaExpiracion());
        return rs;
    }

    @Override
    public SesionUsuario validarToken(String token) {
        Optional<Sesion> optSesion = sesionRepository.findByToken(token);
        if (optSesion.isEmpty() || optSesion.get().getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Su sesión expiró, ingrese de nuevo", HttpStatus.UNAUTHORIZED);
        }
        Optional<Usuario> optUsuario = usuarioRepository.findById(optSesion.get().getUserId().longValue());
        if (optUsuario.isEmpty() || !Boolean.TRUE.equals(optUsuario.get().getActivo())) {
            throw new BadRequestException("El usuario no existe o está inactivo", HttpStatus.UNAUTHORIZED);
        }
        Usuario usuario = optUsuario.get();
        Long medicoId = medicoRepository.findByUsuarioId(usuario.getId()).map(Medico::getId).orElse(null);
        return new SesionUsuario(usuario.getId(), usuario.getUsername(), usuario.getRol(), medicoId);
    }

    @Override
    public LoginRs sesionActual() {
        SesionUsuario sesion = UsuarioActual.get();
        Usuario usuario = usuarioRepository.findById(sesion.getUsuarioId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado", HttpStatus.UNAUTHORIZED));
        return this.armarRespuesta(usuario);
    }

    @Override
    public MiRespuestaRS logout(String token) {
        sesionRepository.findByToken(token).ifPresent(sesionRepository::delete);
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Sesión cerrada");
        return rta;
    }

    @Override
    public String md5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return HexFormat.of().formatHex(md.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 no disponible", e);
        }
    }

    /**
     * Arma la respuesta con perfil, medico asociado y menus.
     *
     * @param usuario usuario autenticado.
     * @return respuesta sin token.
     */
    private LoginRs armarRespuesta(Usuario usuario) {
        LoginRs rs = new LoginRs();
        rs.setUsuarioId(usuario.getId());
        rs.setUsername(usuario.getUsername());
        rs.setPerfilId(usuario.getRol());
        rs.setPerfilNombre(perfilRepository.findById(usuario.getRol()).map(Perfil::getNombre).orElse(""));
        medicoRepository.findByUsuarioId(usuario.getId()).ifPresent(medico -> {
            rs.setMedicoId(medico.getId());
            rs.setMedicoNombre(medico.getNombres() + " " + medico.getApellidos());
        });
        rs.setMenus(menuRepository.findMenusPorPerfil(usuario.getRol()));
        return rs;
    }
}
