package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio de consulta de fórmulas médicas. Las fórmulas las crea el médico
 * desde la atención de la cita (AtencionService).
 */
public interface FormulaMedicaService {

    /** @return fórmulas de la más reciente a la más antigua. */
    List<FormulaMedicaRs> listarFormulasMedicas();

    /** @return detalle de una fórmula con sus medicamentos. */
    FormulaMedicaRs consultarFormula(Long formulaId);

    /**
     * Convierte una formula al objeto de respuesta.
     *
     * @param formula entidad.
     * @param conDetalle si incluye los medicamentos con su disponibilidad.
     * @return respuesta.
     */
    FormulaMedicaRs convertir(FormulaMedica formula, boolean conDetalle);

    /** @return respuesta al inactivar (solo si no se ha despachado nada). */
    MiRespuestaRS inactivarFormula(Long formulaId);

    /** @return PDF de la fórmula. */
    byte[] formulaPdf(Long formulaId);
}
