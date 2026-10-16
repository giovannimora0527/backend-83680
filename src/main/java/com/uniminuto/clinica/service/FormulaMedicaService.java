package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/** Define las operaciones de negocio para fórmulas médicas. */
public interface FormulaMedicaService {
    /** Crea una fórmula médica evitando duplicados en la misma cita. */
    MiRespuestaRS crearFormulaMedica(FormulaMedicaRq formulaRq);

    /** Lista las fórmulas médicas registradas. */
    List<FormulaMedica> listarFormulasMedicas();

    /** Consulta una fórmula médica por su identificador. */
    FormulaMedica consultarFormulaMedica(Long formulaId);

    /** Actualiza los datos permitidos de una fórmula médica. */
    MiRespuestaRS actualizarFormulaMedica(FormulaMedicaRq formulaRq);

    /** Inactiva una fórmula médica en lugar de eliminarla. */
    MiRespuestaRS inactivarFormulaMedica(Long formulaId);
}
