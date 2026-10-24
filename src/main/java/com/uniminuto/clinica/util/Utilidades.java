package com.uniminuto.clinica.util;

import java.security.SecureRandom;

public class Utilidades {
    /**
     * Genera un token OTP (One-Time Password) de 6 caracteres alfanuméricos.
     * @return Token OTP generado.
     */
    private static final String CARACTERES_OTP = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    /**
     * Longitud del token OTP.
     */
    private static final int LONGITUD_OTP = 6;
    /**
     * Generador de números aleatorios seguro para la generación de tokens.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generarTokenOtp() {
        StringBuilder token = new StringBuilder(LONGITUD_OTP);
        for (int i = 0; i < LONGITUD_OTP; i++) {
            token.append(CARACTERES_OTP.charAt(RANDOM.nextInt(CARACTERES_OTP.length())));
        }
        return token.toString();
    }
}
