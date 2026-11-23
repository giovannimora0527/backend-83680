package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.Tarifa;
import lombok.Data;

import java.util.List;

/**
 * Lo que ve el medico al abrir una cita: datos de la mascota, antecedentes,
 * servicios que puede cobrar y, si ya se atendio, la historia y la formula.
 */
@Data
public class AtencionInfoRs {
    private CitaRs cita;
    /** Especialidad del medico. */
    private String especialidad;
    /** Historias anteriores de la mascota (antecedentes medicos). */
    private List<HistoriaMedicaRs> antecedentes;
    /** Tarifas activas que aplican (generales o de la especialidad del medico). */
    private List<Tarifa> servicios;
    /** Historia de esta cita si ya fue atendida. */
    private HistoriaMedicaRs historia;
    /** Formula de esta cita si existe. */
    private FormulaMedicaRs formula;
}
