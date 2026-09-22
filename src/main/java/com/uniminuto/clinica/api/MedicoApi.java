package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Interfaz que define los endpoints asociados al personal médico que labora en la clínica veterinaria.
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medico")
public interface MedicoApi {

    /**
     * Obtiene el listado completo de los médicos veterinarios vinculados al sistema.
     *
     * @return ResponseEntity con una lista de objetos Medico en formato JSON.
     * @throws BadRequestException si se presenta algún error en la petición o en el servidor.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Medico>> listarMedicos()
            throws BadRequestException;
}