package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato de los servicios de prueba de la aplicación.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/clinica")
public interface ClinicaApi {

    /**
     * Metodo test del servicio.
     *
     * @return Servicio funcionando correctamente.
     */
    @GetMapping(value = "/test", produces = {"text/plain"})
    ResponseEntity<String> testService();

    /**
     * Metodo test del servicio que pasa por la capa de servicio.
     *
     * @return mensaje de texto de la capa de servicio.
     */
    @GetMapping(value = "/test2", produces = {"text/plain"})
    ResponseEntity<String> testService2();

    /**
     * Metodo test del servicio que devuelve un objeto JSON.
     *
     * @return objeto de respuesta de prueba.
     */
    @GetMapping(value = "/test3", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> testService3();
}
