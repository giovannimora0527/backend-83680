package com.uniminuto.clinica.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniminuto.clinica.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtTokenService {
    private static final String ALGORITHM = "HmacSHA256";
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationMs;

    public JwtTokenService(
            ObjectMapper objectMapper,
            @Value("${clinica.jwt.secret}") String secret,
            @Value("${clinica.jwt.expiration-ms:28800000}") long expirationMs) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 bytes");
        }
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public TokenClaims emitir(Long userId, String username, String role) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = Instant.now().plusMillis(expirationMs).getEpochSecond();
        try {
            String header = encode(objectMapper.writeValueAsBytes(Map.of("alg", "HS256", "typ", "JWT")));
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", username);
            payload.put("uid", userId);
            payload.put("role", role);
            payload.put("iat", issuedAt);
            payload.put("exp", expiresAt);
            String body = encode(objectMapper.writeValueAsBytes(payload));
            String unsigned = header + "." + body;
            return new TokenClaims(unsigned + "." + sign(unsigned), userId, username, role, expiresAt * 1000);
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible emitir el token de sesión", e);
        }
    }

    public TokenClaims validar(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BadRequestException("Token inválido");
            }
            String unsigned = parts[0] + "." + parts[1];
            byte[] expected = Base64.getUrlDecoder().decode(sign(unsigned));
            byte[] received = Base64.getUrlDecoder().decode(parts[2]);
            if (!MessageDigest.isEqual(expected, received)) {
                throw new BadRequestException("Firma del token inválida");
            }
            Map<?, ?> payload = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), Map.class);
            Number expiration = (Number) payload.get("exp");
            Number userId = (Number) payload.get("uid");
            if (expiration == null || expiration.longValue() <= Instant.now().getEpochSecond()
                    || userId == null || payload.get("sub") == null || payload.get("role") == null) {
                throw new BadRequestException("Token vencido o incompleto");
            }
            return new TokenClaims(token, userId.longValue(), payload.get("sub").toString(),
                    payload.get("role").toString(), expiration.longValue() * 1000);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Token inválido");
        }
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(secret, ALGORITHM));
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    public record TokenClaims(String token, Long userId, String username, String role, long expiresAt) {}
}
