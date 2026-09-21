package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los servicios de las formulas medicas.
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    /**
     * Servicio con la logica de las formulas medicas.
     */
    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * Lista las formulas medicas de la mas reciente a la mas antigua.
     *
     * @return las formulas medicas registradas.
     */
    @Override
    public ResponseEntity<List<FormulaMedicaRs>> listarFormulasMedicas() {
        return ResponseEntity.ok(formulaMedicaService.listarFormulasMedicas());
    }
}
