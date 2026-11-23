package com.uniminuto.clinica.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo de respuesta para una historia médica.
 */
@Data
public class HistoriaMedicaRs {

    /**
     * Identificador de la historia médica.
     */
    private Long id;

    /**
     * Identificador del paciente asociado.
     */
    private Integer pacienteId;

    /**
     * Fecha de creación de la historia médica.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Lista de anotaciones asociadas a la historia médica.
     */
    private List<AnotacionHistoriaRs> anotaciones;
}