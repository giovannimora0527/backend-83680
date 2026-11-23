package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.TarifaLogRs;
import com.uniminuto.clinica.models.TarifaRq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de tarifas de servicios.
 */
@Tag(name = "Tarifas", description = "Tarifas por servicio y especialidad con IVA y log de cambios")
@RequestMapping("/tarifa")
public interface TarifaApi {

    @Operation(summary = "Listar tarifas")
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Tarifa>> listarTarifas();

    @Operation(summary = "Registrar tarifa (Administrador)")
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> guardarTarifa(@RequestBody TarifaRq rq);

    @Operation(summary = "Editar tarifa (Administrador, queda en el log)")
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarTarifa(@RequestBody TarifaRq rq);

    @Operation(summary = "Activar o inactivar tarifa (Administrador)")
    @PatchMapping(value = "/estado", produces = "application/json")
    ResponseEntity<MiRespuestaRS> cambiarEstado(@RequestParam Long id, @RequestParam Boolean activo);

    @Operation(summary = "Historial de cambios de una tarifa")
    @GetMapping(value = "/log", produces = "application/json")
    ResponseEntity<List<TarifaLogRs>> listarLog(@RequestParam Long tarifaId);
}
