package com.uniminuto.clinica.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilidadesTests {

    @Test
    void generarTokenOtpDebeRetornarSeisCaracteresAlfanumericos() {
        for (int i = 0; i < 1000; i++) {
            String token = Utilidades.generarTokenOtp();

            assertEquals(6, token.length());
            assertTrue(token.matches("[A-Z0-9]{6}"));
        }
    }
}
