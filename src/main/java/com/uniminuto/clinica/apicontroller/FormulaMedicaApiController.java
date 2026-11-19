package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.FormulaMedicaApi;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/** Controlador que expone las fórmulas médicas y su dispensación. */
public class FormulaMedicaApiController implements FormulaMedicaApi {

    @Autowired
    /** Servicio con la lógica de fórmulas médicas. */
    private FormulaMedicaService formulaMedicaService;

    @Override
    /** Delega la creación de una fórmula médica y responde 201 (Created). */
    public ResponseEntity<MiRespuestaRS> crearFormulaMedica(FormulaMedicaRq formulaRq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formulaMedicaService.crearFormulaMedica(formulaRq));
    }

    @Override
    /** Delega la consulta de fórmulas médicas. */
    public ResponseEntity<List<FormulaMedica>> listarFormulasMedicas() {
        return ResponseEntity.ok(formulaMedicaService.listarFormulasMedicas());
    }

    @Override
    /** Delega la consulta de fórmulas médicas asociadas a una cita. */
    public ResponseEntity<List<FormulaMedica>> listarFormulasPorCita(Long citaId) {
        return ResponseEntity.ok(formulaMedicaService.listarFormulasPorCita(citaId));
    }

    @Override
    /** Delega la consulta de fórmulas médicas asociadas a una mascota. */
    public ResponseEntity<List<FormulaMedica>> listarFormulasPorMascota(Integer mascotaId) {
        return ResponseEntity.ok(formulaMedicaService.listarFormulasPorMascota(mascotaId));
    }

    @Override
    /** Delega la consulta de una fórmula médica. */
    public ResponseEntity<FormulaMedica> consultarFormulaMedica(Long formulaId) {
        return ResponseEntity.ok(formulaMedicaService.consultarFormulaMedica(formulaId));
    }

    @Override
    /** Delega la actualización de una fórmula médica. */
    public ResponseEntity<MiRespuestaRS> actualizarFormulaMedica(FormulaMedicaRq formulaRq) {
        return ResponseEntity.ok(formulaMedicaService.actualizarFormulaMedica(formulaRq));
    }

    @Override
    /** Delega la inactivación de una fórmula médica. */
    public ResponseEntity<MiRespuestaRS> inactivarFormulaMedica(Long formulaId) {
        return ResponseEntity.ok(formulaMedicaService.inactivarFormulaMedica(formulaId));
    }

    @Override
    /** Delega la dispensación real del medicamento asociado a la fórmula. */
    public ResponseEntity<MiRespuestaRS> dispensarFormula(Long formulaId, Integer cantidad,
                                                          String observacion, String usuario) {
        return ResponseEntity.ok(formulaMedicaService.dispensarFormula(formulaId, cantidad, observacion, usuario));
    }

    @Override
    /** Delega la consulta del historial de movimientos de la fórmula. */
    public ResponseEntity<List<MovimientoMedicamento>> consultarHistorialPorFormula(Long formulaId) {
        return ResponseEntity.ok(formulaMedicaService.consultarHistorialPorFormula(formulaId));
    }
}
