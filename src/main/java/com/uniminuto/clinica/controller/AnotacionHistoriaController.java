package com.uniminuto.clinica.controller;

import com.uniminuto.clinica.dto.AnotacionHistoriaDTO;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para anotaciones de historia médica.
 */
@RestController
@RequestMapping("/anotaciones-historia")
public class AnotacionHistoriaController {

    /** Servicio de anotaciones. */
    private final AnotacionHistoriaService service;

    /**
     * Constructor del controlador.
     *
     * @param service servicio de anotaciones.
     */
    public AnotacionHistoriaController(AnotacionHistoriaService service) {
        this.service = service;
    }

    /**
     * Lista todas las anotaciones.
     *
     * @return lista de anotaciones.
     */
    @GetMapping
    public List<AnotacionHistoriaDTO> listar() {
        return service.listar();
    }

    /**
     * Filtra anotaciones por rango de fechas.
     *
     * @param fechaInicial fecha inicial.
     * @param fechaFinal fecha final.
     * @return lista de anotaciones filtradas.
     */
    @GetMapping("/rango")
    public List<AnotacionHistoriaDTO> listarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFinal) {

        return service.listarPorRango(fechaInicial, fechaFinal);
    }

    /**
     * Crea una anotación.
     *
     * @param dto datos de la anotación.
     * @return anotación creada.
     */
    @PostMapping
    public AnotacionHistoriaDTO crear(
            @Valid @RequestBody AnotacionHistoriaDTO dto) {

        return service.crear(dto);
    }

    /**
     * Actualiza una anotación.
     *
     * @param id identificador de la anotación.
     * @param dto nuevos datos.
     * @return anotación actualizada.
     */
    @PutMapping("/{id}")
    public AnotacionHistoriaDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AnotacionHistoriaDTO dto) {

        return service.actualizar(id, dto);
    }
}
