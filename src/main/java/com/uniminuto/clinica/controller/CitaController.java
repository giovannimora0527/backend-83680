package com.uniminuto.clinica.controller;

import com.uniminuto.clinica.dto.CitaDTO;
import com.uniminuto.clinica.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST de citas.
 */
@RestController
@RequestMapping("/citas")
public class CitaController {

    /** Servicio de citas. */
    private final CitaService service;

    /**
     * Constructor del controlador.
     *
     * @param service servicio de citas.
     */
    public CitaController(CitaService service) {
        this.service = service;
    }

    /**
     * Filtra citas por fecha inicial y fecha final.
     *
     * @param fechaInicial fecha inicial.
     * @param fechaFinal fecha final.
     * @return lista de citas.
     */
    @GetMapping
    public List<CitaDTO> listarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFinal) {

        return service.listarPorRango(fechaInicial, fechaFinal);
    }

    /**
     * Crea una nueva cita.
     *
     * @param dto datos de la nueva cita.
     * @return cita creada.
     */
    @PostMapping
    public CitaDTO crear(@Valid @RequestBody CitaDTO dto) {
        return service.crear(dto);
    }

    /**
     * Actualiza una cita almacenada.
     *
     * @param id identificador de la cita.
     * @param dto nuevos datos.
     * @return cita actualizada.
     */
    @PutMapping("/{id}")
    public CitaDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaDTO dto) {

        return service.actualizar(id, dto);
    }
}
