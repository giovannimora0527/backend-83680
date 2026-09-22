package com.uniminuto.clinica.controller;

import com.uniminuto.clinica.dto.FormulaMedicaDTO;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de fórmulas médicas.
 */
@RestController
@RequestMapping("/formulas-medicas")
public class FormulaMedicaController {

    /** Servicio de fórmulas médicas. */
    private final FormulaMedicaService service;

    /**
     * Constructor del controlador.
     *
     * @param service servicio de fórmulas.
     */
    public FormulaMedicaController(FormulaMedicaService service) {
        this.service = service;
    }

    /**
     * Lista fórmulas médicas de la más reciente a la más antigua.
     *
     * @return respuesta HTTP con las fórmulas.
     */
    @GetMapping
    public ResponseEntity<List<FormulaMedicaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
