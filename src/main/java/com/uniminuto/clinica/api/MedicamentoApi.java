package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoRs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de medicamentos y productos del inventario.
 */
@Tag(name = "Inventario - Medicamentos", description = "Consulta y registro de medicamentos y productos")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    /** @return medicamentos (lista simple para combos). */
    @Operation(summary = "Listar medicamentos (simple)")
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Medicamento>> listarMedicamentos();

    /** @return inventario con existencias, precios y alertas. */
    @Operation(summary = "Inventario con existencias, precios y alertas (agotado, stock bajo, por vencer, vencido)")
    @GetMapping(value = "/inventario", produces = "application/json")
    ResponseEntity<List<MedicamentoRs>> listarInventario();

    /**
     * @param medicamentoId id.
     * @return detalle con lotes.
     */
    @Operation(summary = "Detalle de un medicamento con sus lotes")
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<MedicamentoRs> consultarMedicamento(@RequestParam Long medicamentoId);

    /**
     * @param medicamentoId id.
     * @return historial de entradas y salidas.
     */
    @Operation(summary = "Historial de entradas y salidas de un medicamento")
    @GetMapping(value = "/movimientos", produces = "application/json")
    ResponseEntity<List<MovimientoRs>> listarMovimientos(@RequestParam Long medicamentoId);

    /**
     * @param rq datos.
     * @return respuesta.
     */
    @Operation(summary = "Registrar medicamento o producto (solo Administrador)")
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> guardarMedicamento(@RequestBody MedicamentoRq rq);

    /**
     * @param rq datos.
     * @return respuesta.
     */
    @Operation(summary = "Editar medicamento o producto (solo Administrador)")
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarMedicamento(@RequestBody MedicamentoRq rq);
}
