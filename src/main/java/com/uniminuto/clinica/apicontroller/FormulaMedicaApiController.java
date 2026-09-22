package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST encargado de exponer los servicios web para las fórmulas médicas del inventario[cite: 15].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@RestController
@RequestMapping("/api/formula-medica")
public class FormulaMedicaApiController {

    /** Servicio de lógica de negocio para las fórmulas médicas[cite: 15]. */
    @Autowired
    private FormulaMedicaService formulaMedicaService;

    /**
     * Requerimiento 1: Obtiene el listado de fórmulas médicas ordenadas por fecha de creación de la más reciente a la más antigua[cite: 15].
     *
     * @return ResponseEntity con la lista de fórmulas médicas ordenadas[cite: 15].
     */
    @GetMapping
    public ResponseEntity<List<FormulaMedica>> listarFormulasMedicas() {
        return ResponseEntity.ok(formulaMedicaService.obtenerTodasOrdenadasPorFecha());
    }
}