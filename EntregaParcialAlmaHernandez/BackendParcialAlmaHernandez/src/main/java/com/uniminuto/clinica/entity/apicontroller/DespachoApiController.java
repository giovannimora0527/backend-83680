package com.uniminuto.clinica.entity.apicontroller;

import com.uniminuto.clinica.api.DespachoApi;
import com.uniminuto.clinica.model.DespachoConfirmarRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.DespachoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.security.Principal;

@RestController
public class DespachoApiController implements DespachoApi {

    @Autowired
    private DespachoService despachoService;

    @Override
    @PreAuthorize("hasRole('DESPACHO_MEDICO')")
    public ResponseEntity<List<Map<String, Object>>> listarPendientes() {
        return ResponseEntity.ok(despachoService.listarPendientes());
    }

    @Override
    @PreAuthorize("hasRole('DESPACHO_MEDICO')")
    public ResponseEntity<RespuestaRs> confirmar(
            Principal principal,
            DespachoConfirmarRq request
    ) throws BadRequestException {
        return ResponseEntity.ok(despachoService.confirmar(principal.getName(), request));
    }
}
