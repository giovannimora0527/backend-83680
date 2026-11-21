package com.uniminuto.clinica.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniminuto.clinica.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenServiceTest {
    private static final String SECRET = "a-test-secret-longer-than-thirty-two-bytes";

    @Test
    void emiteYValidaTokenFirmado() {
        JwtTokenService service = new JwtTokenService(new ObjectMapper(), SECRET, 60_000);

        JwtTokenService.TokenClaims claims = service.emitir(12L, "admin", "ADMINISTRADOR");
        JwtTokenService.TokenClaims validated = service.validar(claims.token());

        assertEquals(12L, validated.userId());
        assertEquals("admin", validated.username());
        assertEquals("ADMINISTRADOR", validated.role());
    }

    @Test
    void rechazaFirmaManipuladaYTokenVencido() {
        JwtTokenService service = new JwtTokenService(new ObjectMapper(), SECRET, 60_000);
        String token = service.emitir(12L, "admin", "ADMINISTRADOR").token();
        String[] parts = token.split("\\.");
        parts[1] = parts[1] + "x";

        assertThrows(BadRequestException.class, () -> service.validar(String.join(".", parts)));
        assertThrows(BadRequestException.class, () ->
                new JwtTokenService(new ObjectMapper(), SECRET, 0)
                        .validar(new JwtTokenService(new ObjectMapper(), SECRET, 0)
                                .emitir(12L, "admin", "ADMINISTRADOR").token()));
    }
}
