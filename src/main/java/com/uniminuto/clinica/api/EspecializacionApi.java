package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Especializacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Rutas de los servicios de las especializaciones.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/especializacion")
public interface EspecializacionApi {

    /**
     * Lista las especializaciones (para el combo de medicos).
     *
     * @return lista de especializaciones.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Especializacion>> listarEspecializaciones();
}
