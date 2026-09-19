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

    // Obtiene todas las historias médicas.
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<HistoriaMedica>> listarHistoriasMedicas() throws BadRequestException;

    // Registra una nueva historia médica.
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearHistoriaMedica(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    // Actualiza una historia médica existente.
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarHistoriaMedica(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    // Elimina una historia médica por su identificador.
    @DeleteMapping(value = "/eliminar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> eliminarHistoriaMedica(@RequestParam Long historiaMedicaId)
            throws BadRequestException;
}
