package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de la lógica de negocio para las fórmulas médicas.
 * Gestiona las operaciones de consulta y transformación de datos.
 *
 * @author Alma Hernandez
 */
@Service

public class FormulaMedicaServiceImpl implements FormulaMedicaService {
    /**
     * Repositorio inyectado para acceder a los datos de fórmulas médicas.
     */
    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    /**
     * Obtiene la lista de todas las fórmulas médicas registradas,
     * ordenadas por fecha de creación de forma descendente.
     */
    @Override
    public List<FormulaMedica> listarFormulasMedicas() throws BadRequestException {
        try {
            return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
        } catch (Exception e) {
            throw new BadRequestException("Error al listar las fórmulas médicas: " + e.getMessage());
        }
    }
}