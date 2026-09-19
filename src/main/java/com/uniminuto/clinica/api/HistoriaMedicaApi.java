package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<HistoriaMedica>> listarHistoriasMedicas() throws BadRequestException;

    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearHistoriaMedica(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    @PostMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarHistoriaMedica(@RequestBody HistoriaMedicaRq historiaMedicaRq)
            throws BadRequestException;

    @PostMapping(value = "/eliminar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> eliminarHistoriaMedica(@RequestParam Long historiaMedicaId)
            throws BadRequestException;
}
