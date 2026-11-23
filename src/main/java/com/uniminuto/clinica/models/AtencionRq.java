package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos que registra el medico al atender una cita.
 */
@Data
public class AtencionRq {
    /** Cita que se atiende. */
    private Long citaId;
    /** Servicio prestado (tarifa que se cobrara). */
    private Long tarifaId;
    private String motivo;
    private String sintomas;
    private String diagnostico;
    private String observaciones;
    private String recomendaciones;
    /** Indicaciones generales de la formula. */
    private String indicacionesFormula;
    /** Medicamentos formulados (puede ir vacio si no se formula nada). */
    private List<Medicamento> medicamentos = new ArrayList<>();

    /**
     * Medicamento prescrito en la atencion.
     */
    @Data
    public static class Medicamento {
        private Long medicamentoId;
        private String dosis;
        private String frecuencia;
        private Integer duracionDias;
        private Integer cantidad;
        private String viaAdministracion;
        private String indicaciones;
    }
}
