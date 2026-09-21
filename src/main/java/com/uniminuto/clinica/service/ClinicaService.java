package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.MiRespuestaRS;

/**
 * Servicio con los métodos de prueba para verificar que la aplicación funciona.
 */
public interface ClinicaService {

    /**
     * Devuelve un mensaje de texto de prueba.
     *
     * @return mensaje de que el servicio está funcionando.
     */
    String testService2();

    /**
     * Devuelve un objeto de respuesta de prueba.
     *
     * @return respuesta con estado 200 y un mensaje de prueba.
     */
    MiRespuestaRS testService3();
}
