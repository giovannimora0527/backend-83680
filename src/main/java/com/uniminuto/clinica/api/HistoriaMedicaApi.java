package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    /**
     * Obtiene todas las historias médicas, de la consulta más reciente a la más antigua.
     *
     * @return lista de historias médicas.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<HistoriaMedica>> listarHistoriasMedicas() throws BadRequestException;

    /**
     * Consulta una historia médica por su identificador.
     *
     * @param historiaMedicaId identificador de la historia.
     * @return historia médica encontrada.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<HistoriaMedica> consultarHistoriaMedica(@RequestParam Long historiaMedicaId)
            throws BadRequestException;

    /**
     * Registra una nueva historia médica a partir de una cita atendida.
     *
     * @param historiaMedicaRq datos de la historia.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearHistoriaMedica(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    /**
     * Actualiza el diagnóstico, las observaciones y el estado de una historia abierta.
     *
     * @param historiaMedicaRq datos de la historia.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarHistoriaMedica(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    /**
     * Elimina una historia médica que no tenga anotaciones.
     *
     * @param historiaMedicaId identificador de la historia.
     * @return respuesta con el resultado.
     * @throws BadRequestException excepcion.
     */
    @DeleteMapping(value = "/eliminar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> eliminarHistoriaMedica(@RequestParam Long historiaMedicaId)
            throws BadRequestException;
}
