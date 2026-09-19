package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

/** Define operaciones básicas de prueba del servicio. */
public interface ClinicaService {

    /** Devuelve un mensaje de prueba. */
    /** Devuelve una respuesta de prueba estructurada. */
    String testService2() throws BadRequestException;


    MiRespuestaRS testService3() throws BadRequestException;

}
