package com.uniminuto.clinica.models;

import lombok.Data;

@Data
/** Respuesta estándar enviada después de una operación. */
public class MiRespuestaRS {

    /** Código de estado de la operación. */
    private int status;
    /** Mensaje descriptivo del resultado. */
    private String message;
}
