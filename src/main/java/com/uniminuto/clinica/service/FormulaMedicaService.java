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
}
