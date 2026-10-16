package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de los servicios de las historias clinicas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    /**
     * Lista las historias clinicas de la mas reciente a la mas antigua.
     *
     * @return lista de historias.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<HistoriaMedicaRs>> listarHistorias();

    /**
     * Consulta una historia clinica por su id.
     *
     * @param historiaId id de la historia.
     * @return la historia.
     */
    @GetMapping(value = "/consultar", produces = {"application/json"})
    ResponseEntity<HistoriaMedicaRs> consultarHistoria(@RequestParam Long historiaId);

    /**
     * Crea una historia clinica a partir de una cita atendida.
     *
     * @param historiaRq datos de la historia.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarHistoria(@RequestBody HistoriaMedicaRq historiaRq);
}
