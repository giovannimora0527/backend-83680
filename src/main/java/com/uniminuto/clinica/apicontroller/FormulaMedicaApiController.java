package com.uniminuto.clinica.apicontroller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.service.FormulaMedicaService;

/**
 * Controlador REST encargado de gestionar las solicitudes
 * relacionadas con las fórmulas médicas.
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * Lista las fórmulas médicas ordenadas desde la más reciente
     * hasta la más antigua.
     *
     * @return respuesta HTTP con la lista de fórmulas médicas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public ResponseEntity<List<FormulaMedica>> listarFormulaMedicas()
            throws BadRequestException {

        List<FormulaMedica> formulaMedicas =
                formulaMedicaService.obtenerFormulaMedicas();

        return ResponseEntity.ok(formulaMedicas);
    }
}