package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de finalizar una atencion.
 */
@Data
public class AtencionRs {
    private String message;
    private Long historiaId;
    /** Formula generada (null si no se formulo nada). */
    private Long formulaId;
    /** Avisos, por ejemplo medicamentos sin existencias suficientes. */
    private List<String> advertencias = new ArrayList<>();
}
