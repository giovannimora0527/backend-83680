package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/anotacion-historia")
public interface AnotacionHistoriaApi {
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearAnotacion(@RequestBody AnotacionHistoriaRq anotacionRq) throws BadRequestException;

    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<AnotacionHistoria>> listarAnotacionesPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFinal) throws BadRequestException;

    @PostMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarAnotacion(@RequestBody AnotacionHistoriaRq anotacionRq) throws BadRequestException;
}
