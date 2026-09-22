package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.coyote.BadRequestException;

/**
 * Interfaz que define los endpoints de prueba y verificación general para el sistema de la clínica.
 * Expone servicios básicos de diagnóstico bajo el contexto base /clinica.
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/clinica")
public interface ClinicaApi {

    /**
     * Metodo de prueba inicial que verifica la disponibilidad del servicio web.
     *
     * @return ResponseEntity con un mensaje de texto indicando que el servicio funciona correctamente.
     * @throws BadRequestException si ocurre un error en la solicitud o validación de parámetros.
     */
    @GetMapping(value = "/test",
            produces = {"application/text"},
            consumes = {"application/json"})
    ResponseEntity<String> testService()
            throws BadRequestException;


    /**
     * Segundo métedo de prueba para validación de rutas y conectividad del servidor.
     *
     * @return ResponseEntity con un mensaje de texto de confirmación secundaria.
     * @throws BadRequestException si se presenta algún inconveniente al procesar la petición.
     */
    @GetMapping(value = "/test2",
            produces = {"application/text"},
            consumes = {"application/json"})
    ResponseEntity<String> testService2()
            throws BadRequestException;

    /**
     * Metodo de prueba avanzado que retorna una respuesta estructurada en formato JSON.
     *
     * @return ResponseEntity con un objeto de tipo MiRespuestaRS que contiene el estado de la prueba.
     * @throws BadRequestException si la petición es incorrecta o no cumple con el formato esperado.
     */
    @GetMapping(value = "/test3",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> testService3()
            throws BadRequestException;
}