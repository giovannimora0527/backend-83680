package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para la gestión de las citas médicas en la clínica veterinaria.
 * Permite filtrar por fechas y realizar operaciones de creación y actualización[cite: 14].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cita")
public class CitaApiController {

    /** Servicio que encapsula la lógica de negocio para las citas[cite: 14]. */
    @Autowired
    private CitaService citaService;

    /**
     * Requerimientos 2 y 3: Filtra las citas del sistema dado un rango de fechas, ordenadas de la más reciente a la más antigua[cite: 14].
     *
     * @param fechaInicio Fecha y hora inicial de la consulta[cite: 14].
     * @param fechaFin Fecha y hora final de la consulta[cite: 14].
     * @return ResponseEntity con la lista de citas filtradas[cite: 14].
     */
    @GetMapping("/filtrar")
    public ResponseEntity<List<Cita>> filtrarCitas(
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(citaService.filtrarPorRangoFechas(fechaInicio, fechaFin));
    }

    /**
     * Requerimiento 4a: Permite adicionar una nueva cita al sistema[cite: 14].
     *
     * @param cita Objeto Cita recibido en el cuerpo de la petición[cite: 14].
     * @return ResponseEntity con la cita creada y el estado HTTP CREATED[cite: 14].
     */
    @PostMapping
    public ResponseEntity<Cita> crearCita(@RequestBody Cita cita) {
        return new ResponseEntity<>(citaService.guardarCita(cita), HttpStatus.CREATED);
    }

    /**
     * Requerimiento 4b: Permite actualizar una cita existente en el sistema[cite: 14].
     *
     * @param id Identificador de la cita a modificar[cite: 14].
     * @param cita Objeto Cita con los datos actualizados[cite: 14].
     * @return ResponseEntity con la cita modificada y estado HTTP OK[cite: 14].
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizarCita(@PathVariable("id") Long id, @RequestBody Cita cita) {
        return ResponseEntity.ok(citaService.actualizarCita(id, cita));
    }
}