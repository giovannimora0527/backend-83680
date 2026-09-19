package com.uniminuto.clinica.serviceimpl;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;

/**
 * Implementa las operaciones relacionadas con las fórmulas médicas.
 */
@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    /**
     * Obtiene las fórmulas médicas ordenadas desde la más reciente
     * hasta la más antigua según la fecha de creación.
     *
     * @return lista de fórmulas médicas ordenadas por fecha descendente.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public List<FormulaMedica> obtenerFormulaMedicas() throws BadRequestException {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }

}