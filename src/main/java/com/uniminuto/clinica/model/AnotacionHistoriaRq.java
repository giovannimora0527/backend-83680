
package com.uniminuto.clinica.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * Modelo de solicitud para actualizar una anotación
 * de una historia médica.
 */
@Data
public class AnotacionHistoriaRq {

    @NotBlank(message = "La descripción de la anotación es obligatoria.")
    private String descripcion;
}
