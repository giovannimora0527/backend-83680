package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio con la lógica de negocio de las fórmulas médicas.
 */
public interface FormulaMedicaService {

    /**
     * Lista las fórmulas médicas ordenadas por fecha de creación,
     * de la más reciente a la más antigua.
     *
     * @return las fórmulas médicas registradas.
     */
    List<FormulaMedicaRs> listarFormulasMedicas();

    /**
     * Consulta una fórmula por su id.
     *
     * @param formulaId id de la fórmula.
     * @return la fórmula.
     */
    FormulaMedicaRs consultarFormula(Long formulaId);

    /**
     * Crea una fórmula validando las reglas de negocio.
     *
     * @param formulaRq datos de la fórmula.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarFormula(FormulaMedicaRq formulaRq);

    /**
     * Edita los datos permitidos de una fórmula activa.
     *
     * @param formulaRq datos de la fórmula con su id.
     * @return respuesta de éxito.
     */
    MiRespuestaRS actualizarFormula(FormulaMedicaRq formulaRq);

    /**
     * Inactiva una fórmula (no se borra físicamente).
     *
     * @param formulaId id de la fórmula.
     * @return respuesta de éxito.
     */
    MiRespuestaRS inactivarFormula(Long formulaId);
}
