package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.OtpToken;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AuthRq;
import com.uniminuto.clinica.models.AuthRs;
import com.uniminuto.clinica.repository.OtpTokenRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.AuthService;
import com.uniminuto.clinica.service.EmailService;
import com.uniminuto.clinica.util.PasswordUtil;
import com.uniminuto.clinica.util.Utilidades;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String FROM = "clinicaveterinariauniminutosis@gmail.com";

    private static final Integer OTP_TOKEN_EXPIRATION_MINUTES = 15; // Token expiration time in minutes

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;


    @Override
    public AuthRs login(AuthRq authRq) throws BadRequestException {
        this.validateAuthRq(authRq);
        Optional<Usuario> optUser = this.usuarioRepository.findByUsername(authRq.getUsername());
        if (optUser.isEmpty()) {
            throw new BadRequestException("Usuario o Password no encontrado");
        }

        Usuario user = optUser.get();
        String passEncrypted = PasswordUtil.generarMD5(authRq.getPassword());
        if (!user.getPasswordHash().equals(passEncrypted)) {
            throw new BadRequestException("Usuario o Password no encontrado");
        }

        // Envio Token OTP al correo del usuario
        String tokenOtp = Utilidades.generarTokenOtp();
        String bodyHtml = this.generarHtmlBodyCorreo(user.getUsername(), tokenOtp);
        this.emailService.enviarCorreo(user.getEmail(), "Token OTP", bodyHtml, FROM);

        OtpToken token = new OtpToken();
        token.setUsuarioId(user.getId());
        token.setCodigo(tokenOtp);
        token.setUsado(Boolean.FALSE);
        token.setIntentos(0);
        token.setFechaCreacion(LocalDateTime.now());
        token.setFechaExpiracion(LocalDateTime.now()
                .plusMinutes(OTP_TOKEN_EXPIRATION_MINUTES));
        this.otpTokenRepository.save(token);

        AuthRs rta = new AuthRs();
        rta.setUsuarioId(user.getId());
        rta.setLogin(Boolean.TRUE);
        rta.setUsername(user.getUsername());



        return rta;
    }

    private String generarHtmlBodyCorreo(String username, String otpToken) {
        return "Estimado usuario " + username + ",\n\n" +
                "Su token OTP es: " + otpToken + "\n\n" +
                "Por favor, utilice este token para completar su autenticación.\n\n" +
                "Saludos,\n" +
                "Equipo de Soporte";

    }

    private void validateOtpToken(String otpToken, Long usuarioId) throws BadRequestException {
        // Implement the OTP token validation logic here
    }

    private void validateAuthRq(AuthRq authRq) throws BadRequestException {
        // Implement the AuthRq validation logic here
        if (authRq.getUsername() == null || authRq.getPassword() == null) {
            throw new BadRequestException("Usuario o password no puede ser nulo");
        }

        if (authRq.getUsername().isEmpty() || authRq.getPassword().isEmpty()) {
            throw new BadRequestException("Usuario o password no puede estar vacío");
        }
    }
}
