
package com.uniminuto.clinica.api;

import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.CrearAnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/historia-medica")
public interface HistoriaMedicaApi {

    @PostMapping(
            value = "/guardar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<RespuestaRs> guardarHistoria(
            @RequestBody @Valid HistoriaMedicaRq historiaRq
    ) throws BadRequestException;

    @PostMapping(
            value = "/anotacion/guardar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<RespuestaRs> guardarAnotacion(
            @RequestBody @Valid CrearAnotacionHistoriaRq anotacionRq
    ) throws BadRequestException;

    @GetMapping(
            value = "/listar",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<List<HistoriaMedicaRs>> listarHistorias(
            @RequestParam("fechaIni") LocalDateTime fechaIni,
            @RequestParam("fechaFin") LocalDateTime fechaFin
    );

    @PutMapping(
            value = "/anotacion/actualizar",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<RespuestaRs> actualizarAnotacion(
            @RequestParam("id") Long id,
            @RequestBody @Valid AnotacionHistoriaRq anotacionRq
    ) throws BadRequestException;
}
