package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion de la logica de las formulas medicas.
 */
@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    /**
     * Repositorio de formulas medicas.
     */
    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    /**
     * Lista las formulas medicas de la mas reciente a la mas antigua.
     *
     * @return las formulas medicas registradas.
     */
    @Override
    public List<FormulaMedicaRs> listarFormulasMedicas() {
        // Paso 1. Consultar las formulas ya ordenadas por fecha de creacion
        List<FormulaMedica> formulas = formulaMedicaRepository
                .findAllByOrderByFechaCreacionRegistroDesc();

        // Paso 2. Convertir cada formula al objeto de respuesta
        List<FormulaMedicaRs> respuesta = new ArrayList<>();
        for (FormulaMedica formula : formulas) {
            FormulaMedicaRs rs = new FormulaMedicaRs();
            rs.setId(formula.getId());
            rs.setCitaId(formula.getCita().getId());
            rs.setMedicamentoId(formula.getMedicamento().getId());
            rs.setMedicamentoNombre(formula.getMedicamento().getNombre());
            rs.setDosis(formula.getDosis());
            rs.setIndicaciones(formula.getIndicaciones());
            rs.setFechaCreacionRegistro(formula.getFechaCreacionRegistro());
            rs.setFechaActualizacionRegistro(formula.getFechaActualizacionRegistro());
            respuesta.add(rs);
        }

        return respuesta;
    }
}
