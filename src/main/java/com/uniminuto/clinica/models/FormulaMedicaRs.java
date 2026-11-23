package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Formula medica (cabecera y medicamentos) para mostrar.
 */
@Data
public class FormulaMedicaRs {
    private Long id;
    private Long citaId;
    private LocalDateTime citaFechaHora;
    private Long historiaId;
    private Integer mascotaId;
    private String mascotaNombre;
    private String mascotaEspecie;
    private Long clienteId;
    private String clienteNombre;
    private String clienteDocumento;
    private Long medicoId;
    private String medicoNombre;
    private LocalDate fechaEmision;
    private LocalDateTime fechaCreacionRegistro;
    private String indicaciones;
    private Boolean activo;
    /** Estado del despacho. */
    private String estadoDespacho;
    /** Resumen de medicamentos para el listado. */
    private String medicamentosResumen;
    private List<FormulaDetalleRs> detalles;
}
