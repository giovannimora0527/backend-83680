package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementación de la interfaz FormulaMedicaService que gestiona la lógica de negocio
 * para el inventario de fórmulas médicas[cite: 22].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    /** Repositorio JPA para la entidad FormulaMedica[cite: 22]. */
    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;

    /**
     * Obtiene todas las fórmulas médicas ordenadas por fecha de creación de la más reciente a la más antigua[cite: 22].
     *
     * @return Lista de fórmulas médicas ordenadas descendentemente[cite: 22].
     */
    @Override
    public List<FormulaMedica> obtenerTodasOrdenadasPorFecha() {
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc();
    }
}