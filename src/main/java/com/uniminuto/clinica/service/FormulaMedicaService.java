package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;

import java.util.List;

/** Define las operaciones de negocio para fórmulas médicas. */
public interface FormulaMedicaService {
    /** Lista las fórmulas médicas registradas. */
    List<FormulaMedica> listarFormulasMedicas();
}
