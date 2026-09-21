package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
import org.springframework.stereotype.Service;

/**
 * Implementacion de los metodos de prueba del servicio.
 */
@Service
public class ClinicaServiceImpl implements ClinicaService {

    /**
     * Devuelve un texto de prueba.
     *
     * @return mensaje de que el servicio funciona.
     */
    @Override
    public String testService2() {
        return "Servicio ok";
    }

    /**
     * Devuelve un objeto de prueba.
     *
     * @return respuesta con estado 200 y un mensaje.
     */
    @Override
    public MiRespuestaRS testService3() {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Servicio ok desde clase y objeto");
        return respuesta;
    }
}
