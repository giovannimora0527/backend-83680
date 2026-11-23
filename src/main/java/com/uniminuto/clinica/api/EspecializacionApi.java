package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.models.MiRespuestaRS;
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
 * Rutas de las especialidades veterinarias.
 */
@Tag(name = "Especialidades", description = "Gestión de especialidades veterinarias")
@RequestMapping("/especializacion")
public interface EspecializacionApi {

    @Operation(summary = "Listar especialidades")
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Especializacion>> listarEspecializaciones();

    @Operation(summary = "Registrar especialidad (Administrador)")
    @PostMapping(value = "/guardar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> guardarEspecializacion(@RequestBody Especializacion especializacion);

    @Operation(summary = "Editar especialidad (Administrador)")
    @PutMapping(value = "/actualizar", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarEspecializacion(@RequestBody Especializacion especializacion);

    @Operation(summary = "Activar o inactivar especialidad (Administrador)")
    @PatchMapping(value = "/estado", produces = "application/json")
    ResponseEntity<MiRespuestaRS> cambiarEstado(@RequestParam Long id, @RequestParam Boolean activo);
}
