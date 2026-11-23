package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.Perfil;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.UsuarioRq;
import com.uniminuto.clinica.models.UsuarioRs;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.PerfilRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.AuthService;
import com.uniminuto.clinica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la administracion de usuarios.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private AuthService authService;

    @Override
    public List<UsuarioRs> listarUsuarios() {
        return usuarioRepository.findAllByOrderByUsernameAsc().stream().map(usuario -> {
            UsuarioRs rs = new UsuarioRs();
            rs.setId(usuario.getId());
            rs.setUsername(usuario.getUsername());
            rs.setEmail(usuario.getEmail());
            rs.setPerfilId(usuario.getRol());
            rs.setPerfilNombre(perfilRepository.findById(usuario.getRol()).map(Perfil::getNombre).orElse(""));
            rs.setActivo(usuario.getActivo());
            rs.setFechaCreacion(usuario.getFechaCreacion());
            medicoRepository.findByUsuarioId(usuario.getId()).ifPresent(medico -> {
                rs.setMedicoId(medico.getId());
                rs.setMedicoNombre(medico.getNombres() + " " + medico.getApellidos());
            });
            return rs;
        }).toList();
    }

    @Override
    public List<Perfil> listarPerfiles() {
        return perfilRepository.findAll().stream().filter(p -> Boolean.TRUE.equals(p.getActivo())).toList();
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarUsuario(UsuarioRq usuarioRq) {
        this.validar(usuarioRq);
        if (usuarioRq.getPassword() == null || usuarioRq.getPassword().length() < 6) {
            throw new BadRequestException("La contraseña debe tener mínimo 6 caracteres");
        }
        if (usuarioRepository.existsByUsername(usuarioRq.getUsername().trim())) {
            throw new BadRequestException("Ya existe un usuario con ese nombre");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioRq.getUsername().trim());
        usuario.setPasswordHash(authService.md5(usuarioRq.getPassword()));
        usuario.setRol(usuarioRq.getPerfilId());
        usuario.setEmail(usuarioRq.getEmail().trim());
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
        this.vincularMedico(usuario, usuarioRq.getMedicoId());
        return this.exito("Usuario creado correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarUsuario(UsuarioRq usuarioRq) {
        this.validar(usuarioRq);
        Usuario usuario = usuarioRepository.findById(usuarioRq.getId() == null ? -1 : usuarioRq.getId())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        Optional<Usuario> otro = usuarioRepository.findByUsername(usuarioRq.getUsername().trim());
        if (otro.isPresent() && !otro.get().getId().equals(usuario.getId())) {
            throw new BadRequestException("Ya existe otro usuario con ese nombre");
        }
        usuario.setUsername(usuarioRq.getUsername().trim());
        usuario.setRol(usuarioRq.getPerfilId());
        usuario.setEmail(usuarioRq.getEmail().trim());
        if (usuarioRq.getActivo() != null) {
            usuario.setActivo(usuarioRq.getActivo());
        }
        // La contrasena solo cambia si la escriben
        if (usuarioRq.getPassword() != null && !usuarioRq.getPassword().isBlank()) {
            if (usuarioRq.getPassword().length() < 6) {
                throw new BadRequestException("La contraseña debe tener mínimo 6 caracteres");
            }
            usuario.setPasswordHash(authService.md5(usuarioRq.getPassword()));
        }
        usuarioRepository.save(usuario);
        this.vincularMedico(usuario, usuarioRq.getMedicoId());
        return this.exito("Usuario actualizado correctamente");
    }

    /**
     * Asocia el usuario a un medico (solo perfil Medico) y quita asociaciones anteriores.
     *
     * @param usuario  usuario guardado.
     * @param medicoId medico a asociar o null.
     */
    private void vincularMedico(Usuario usuario, Long medicoId) {
        medicoRepository.findByUsuarioId(usuario.getId()).ifPresent(anterior -> {
            anterior.setUsuarioId(null);
            medicoRepository.saveAndFlush(anterior);
        });
        if (usuario.getRol() != Perfil.MEDICO) {
            return;
        }
        if (medicoId == null) {
            throw new BadRequestException("Un usuario con perfil Médico debe estar asociado a un médico");
        }
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new BadRequestException("Médico no encontrado"));
        if (medico.getUsuarioId() != null && !medico.getUsuarioId().equals(usuario.getId())) {
            throw new BadRequestException("Ese médico ya tiene otro usuario asociado");
        }
        medico.setUsuarioId(usuario.getId());
        medicoRepository.save(medico);
    }

    /**
     * Valida los campos obligatorios.
     *
     * @param usuarioRq datos.
     */
    private void validar(UsuarioRq usuarioRq) {
        if (usuarioRq == null || usuarioRq.getUsername() == null || !usuarioRq.getUsername().trim().matches("[A-Za-z0-9._-]{3,50}")) {
            throw new BadRequestException("El usuario debe tener entre 3 y 50 caracteres (letras, números, punto o guion)");
        }
        if (usuarioRq.getEmail() == null || !usuarioRq.getEmail().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new BadRequestException("Ingrese un correo válido");
        }
        if (usuarioRq.getPerfilId() == null || perfilRepository.findById(usuarioRq.getPerfilId()).isEmpty()) {
            throw new BadRequestException("Seleccione un perfil válido");
        }
    }

    /**
     * Respuesta de exito.
     *
     * @param mensaje mensaje.
     * @return respuesta.
     */
    private MiRespuestaRS exito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
