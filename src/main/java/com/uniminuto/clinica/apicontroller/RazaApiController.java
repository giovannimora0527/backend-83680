package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.RazaApi;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import com.uniminuto.clinica.service.RazaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de los servicios de las razas.
 */
@RestController
public class RazaApiController implements RazaApi {

    /**
     * Servicio con la logica de las razas.
     */
    @Autowired
    private RazaService razaService;

    /**
     * Guarda una raza nueva.
     *
     * @param razaRq datos de la raza.
     * @return respuesta de exito.
     */
    @Override
    public ResponseEntity<MiRespuestaRS> guardarRaza(RazaRq razaRq) {
        return ResponseEntity.ok(this.razaService.guardarRazaNueva(razaRq));
    }
}
