package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
/** Implementa los servicios básicos de prueba de la clínica. */
public class ClinicaServiceImpl implements ClinicaService {

    @Override
    // Devuelve un mensaje sencillo para comprobar que el servicio funciona.
    public String testService2() throws BadRequestException {
        return "Servicio ok";
    }

    @Override
    // Construye una respuesta con estado y mensaje de prueba.
    public MiRespuestaRS testService3() throws BadRequestException {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Servicio ok desde clase y objeto");
        return respuesta;
    }
}
