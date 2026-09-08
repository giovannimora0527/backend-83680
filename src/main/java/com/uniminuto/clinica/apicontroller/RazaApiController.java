package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.RazaApi;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import com.uniminuto.clinica.service.RazaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RazaApiController implements RazaApi {

    @Autowired
    private RazaService razaService;


    @Override
    public ResponseEntity<MiRespuestaRS> guardarRaza(RazaRq razaRq) throws BadRequestException {
        return ResponseEntity.ok(this.razaService.guardarRazaNueva(razaRq));
    }
}
