package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de las fórmulas médicas, solo delega al servicio.
 */
@RestController
public class FormulaMedicaApiController implements FormulaMedicaApi {

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
    public ResponseEntity<MiRespuestaRS> inactivarFormula(Long formulaId) {
        return ResponseEntity.ok(formulaMedicaService.inactivarFormula(formulaId));
    }

    @Override
    public ResponseEntity<byte[]> formulaPdf(Long formulaId) {
        return Descarga.archivo(formulaMedicaService.formulaPdf(formulaId), "formula_" + formulaId + ".pdf", Descarga.PDF);
    }
}
