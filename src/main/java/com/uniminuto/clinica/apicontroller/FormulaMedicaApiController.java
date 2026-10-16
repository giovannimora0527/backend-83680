package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de las fórmulas médicas, solo delega al servicio.
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

    /** Servicio con la lógica de las fórmulas. */
    @Autowired
    private FormulaMedicaService formulaMedicaService;

    @Override
    public ResponseEntity<List<FormulaMedicaRs>> listarFormulasMedicas() {
        return ResponseEntity.ok(formulaMedicaService.listarFormulasMedicas());
    }

    @Override
    public ResponseEntity<FormulaMedicaRs> consultarFormula(Long formulaId) {
        return ResponseEntity.ok(formulaMedicaService.consultarFormula(formulaId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarFormula(FormulaMedicaRq formulaRq) {
        return ResponseEntity.ok(formulaMedicaService.guardarFormula(formulaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarFormula(FormulaMedicaRq formulaRq) {
        return ResponseEntity.ok(formulaMedicaService.actualizarFormula(formulaRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> inactivarFormula(Long formulaId) {
        return ResponseEntity.ok(formulaMedicaService.inactivarFormula(formulaId));
    }
}
