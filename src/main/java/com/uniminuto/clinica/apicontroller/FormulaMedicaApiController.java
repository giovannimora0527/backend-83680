package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/** Controlador que expone las fórmulas médicas. */
public class FormulaMedicaApiController implements FormulaMedicaApi {

    @Autowired
    /** Servicio con la lógica de fórmulas médicas. */
    private FormulaMedicaService formulaMedicaService;

    @Override
    /** Delega la consulta de fórmulas médicas. */
    public ResponseEntity<List<FormulaMedica>> listarFormulasMedicas() {
        return ResponseEntity.ok(formulaMedicaService.listarFormulasMedicas());
    }
}
