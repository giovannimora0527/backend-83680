
package com.uniminuto.clinica.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Modelo de solicitud para crear una anotación
 * de una historia médica.
 */
@Data
public class CrearAnotacionHistoriaRq {

    @NotBlank(message = "La descripción de la anotación es obligatoria.")
    private String descripcion;

    @NotNull(message = "El identificador de la historia médica es obligatorio.")
    private Long historiaId;

    @NotNull(message = "El identificador del médico es obligatorio.")
    private Integer medicoId;
}
