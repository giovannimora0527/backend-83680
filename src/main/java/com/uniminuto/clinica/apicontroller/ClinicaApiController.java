package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClinicaApi;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de los servicios de prueba.
 */
@RestController
public class ClinicaApiController implements ClinicaApi {

    /**
     * Servicio con la logica de las pruebas.
     */
    @Autowired
    private ClinicaService clinicaService;

    /**
     * Prueba simple del servicio.
     *
     * @return mensaje de que el servicio funciona.
     */
    @Override
    public ResponseEntity<String> testService() {
        return ResponseEntity.ok("Servicio funcionando correctamente");
    }

    /**
     * Prueba del servicio pasando por la capa de servicio.
     *
     * @return mensaje de texto.
     */
    @Override
    public ResponseEntity<String> testService2() {
        return ResponseEntity.ok(clinicaService.testService2());
    }

    /**
     * Prueba del servicio devolviendo un objeto.
     *
     * @return objeto de respuesta.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> testService3() {
        return ResponseEntity.ok(clinicaService.testService3());
    }
}
