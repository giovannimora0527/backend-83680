package com.uniminuto.clinica.api;

import com.uniminuto.clinica.model.DespachoConfirmarRq;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.security.Principal;

@RequestMapping("/despacho")
public interface DespachoApi {
    @GetMapping("/pendientes")
    ResponseEntity<List<Map<String, Object>>> listarPendientes();

    @PostMapping("/confirmar")
    ResponseEntity<RespuestaRs> confirmar(
            Principal principal,
            @RequestBody @Valid DespachoConfirmarRq request
    ) throws BadRequestException;
}
