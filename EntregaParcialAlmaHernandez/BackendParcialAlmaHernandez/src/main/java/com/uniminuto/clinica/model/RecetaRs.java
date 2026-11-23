package com.uniminuto.clinica.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Modelo de respuesta para una receta médica.
 */
@Data
public class RecetaRs {

    /**
     * Identificador de la receta.
     */
    private Long id;

    /**
     * Identificador de la cita asociada.
     */
    private Long citaId;

    /**
     * Identificador del medicamento asociado.
     */
    private Integer medicamentoId;

    /**
     * Dosis prescrita.
     */
    private String dosis;

    /**
     * Indicaciones de la receta.
     */
    private String indicaciones;

    /**
     * Fecha de creación de la receta.
     */
    private LocalDateTime fechaCreacionRegistro;
}