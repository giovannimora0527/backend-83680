package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClinicaApi;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.RestController;

@RestController
/** Controlador utilizado para comprobar el funcionamiento de la API. */
public class ClinicaApiController implements ClinicaApi {

    @Autowired
    /** Servicio principal de la clínica. */
    private ClinicaService clinicaService;

    @Override
    /** Devuelve una respuesta de prueba directa. */
    public ResponseEntity<String> testService() throws BadRequestException {
        return ResponseEntity.ok("Servicio funcionando correctamente");
    }

    @Override
    /** Solicita al servicio un mensaje de prueba. */
    public ResponseEntity<String> testService2() throws BadRequestException {
        return ResponseEntity.ok(clinicaService.testService2());
    }

    @Override
    /** Solicita al servicio una respuesta estructurada de prueba. */
    public ResponseEntity<MiRespuestaRS> testService3()
            throws BadRequestException {
        return ResponseEntity.ok(clinicaService.testService3());
    }
}
