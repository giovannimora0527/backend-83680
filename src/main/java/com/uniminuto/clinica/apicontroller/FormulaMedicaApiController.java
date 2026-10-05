package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.service.FormulaMedicaService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.uniminuto.clinica.api.FormulaMedicaApi;

@RestController
public class FormulaMedicaApiController
        implements FormulaMedicaApi {

    private final FormulaMedicaService formulaMedicaService;

    public FormulaMedicaApiController(
            FormulaMedicaService formulaMedicaService
    ) {
        this.formulaMedicaService = formulaMedicaService;
    }

    @Override
    public ResponseEntity<List<FormulaMedica>> listarFormulas()
            throws BadRequestException {

        return ResponseEntity.ok(
                formulaMedicaService.listarFormulas()
        );
    }

    @Override
    public ResponseEntity<FormulaMedica> crearFormula(
            FormulaMedica formula
    ) throws BadRequestException {

        return ResponseEntity.ok(
                formulaMedicaService.crearFormula(
                        formula
                )
        );
    }

    @Override
    public ResponseEntity<FormulaMedica> actualizarFormula(
            Long id,
            FormulaMedica formula
    ) throws BadRequestException {

        return ResponseEntity.ok(
                formulaMedicaService.actualizarFormula(
                        id,
                        formula
                )
        );
    }
}