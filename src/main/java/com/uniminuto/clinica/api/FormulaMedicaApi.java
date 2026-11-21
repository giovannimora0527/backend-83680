package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/formula-medica")
public interface FormulaMedicaApi {
    // Registra una nueva fórmula médica validando disponibilidad y cantidad solicitada.
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> crearFormulaMedica(@RequestBody FormulaMedicaRq formulaRq);

    /**
     * Lista todas las fórmulas médicas, de la más reciente a la más antigua.
     *
     * @return lista de fórmulas médicas.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<FormulaMedica>> listarFormulasMedicas();

    /**
     * Lista las fórmulas médicas asociadas a una cita o atención médica específica.
     *
     * @param citaId identificador de la cita.
     * @return fórmulas de la cita.
     */
    @GetMapping(value = "/listar-by-cita", produces = "application/json")
    ResponseEntity<List<FormulaMedica>> listarFormulasPorCita(@RequestParam Long citaId);

    /**
     * Lista las fórmulas médicas asociadas a una mascota.
     *
     * @param mascotaId identificador de la mascota.
     * @return fórmulas de la mascota.
     */
    @GetMapping(value = "/listar-by-mascota", produces = "application/json")
    ResponseEntity<List<FormulaMedica>> listarFormulasPorMascota(@RequestParam Integer mascotaId);

    /**
     * Consulta el detalle completo de una fórmula médica.
     *
     * @param formulaId identificador de la fórmula.
     * @return fórmula encontrada.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<FormulaMedica> consultarFormulaMedica(@RequestParam Long formulaId);

    /**
     * Actualiza los datos permitidos de una fórmula médica activa.
     *
     * @param formulaRq datos de la fórmula.
     * @return respuesta con el resultado.
     */
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarFormulaMedica(@RequestBody FormulaMedicaRq formulaRq);

    /**
     * Inactiva una fórmula médica; las fórmulas no se eliminan físicamente.
     *
     * @param formulaId identificador de la fórmula.
     * @return respuesta con el resultado.
     */
    @PatchMapping(value = "/inactivar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> inactivarFormulaMedica(@RequestParam Long formulaId);

    // Registra la dispensación real del medicamento asociado a una fórmula y descuenta del inventario.
    @PostMapping(value = "/{formulaId}/dispensar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> dispensarFormula(@PathVariable Long formulaId,
                                                   @RequestParam Integer cantidad,
                                                   @RequestParam(required = false) String observacion,
                                                   @RequestParam(required = false) String usuario);

    // Consulta el historial de movimientos de inventario asociados a una fórmula.
    @GetMapping(value = "/{formulaId}/historial", produces = "application/json")
    ResponseEntity<List<MovimientoMedicamento>> consultarHistorialPorFormula(@PathVariable Long formulaId);

    @GetMapping(value = "/pendientes-despacho", produces = "application/json")
    ResponseEntity<List<FormulaMedica>> listarPendientesDespacho(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String fecha);

    @PatchMapping(value = "/{formulaId}/preparar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> prepararDespacho(@PathVariable Long formulaId,
                                                   HttpServletRequest request);

    @PostMapping(value = "/{formulaId}/despachar", produces = "application/json")
    ResponseEntity<MiRespuestaRS> despacharFormula(@PathVariable Long formulaId,
                                                   @RequestParam Integer cantidad,
                                                   @RequestParam(required = false) String observacion,
                                                   HttpServletRequest request);

    @GetMapping(value = "/{formulaId}/comprobante-pdf", produces = "application/pdf")
    ResponseEntity<byte[]> generarComprobanteDespacho(@PathVariable Long formulaId);
}
