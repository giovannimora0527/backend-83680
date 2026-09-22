package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interfaz para la gestión y administración de las razas registradas en la clínica veterinaria.
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/raza")
public interface RazaApi {

    /**
     * Almacena una nueva raza en el sistema de información de la clínica.
     *
     * @param razaRq Objeto de transferencia de datos (Request) que contiene los detalles de la raza a guardar.
     * @return ResponseEntity con un objeto MiRespuestaRS que confirma el resultado del almacenamiento.
     * @throws BadRequestException si los datos de la raza proporcionados son erróneos o incompletos.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarRaza(
            @RequestBody RazaRq razaRq)
            throws BadRequestException;
}