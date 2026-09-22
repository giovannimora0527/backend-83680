package com.uniminuto.clinica.service;

import com.uniminuto.clinica.dto.FormulaMedicaDTO;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de negocio para fórmulas médicas.
 */
@Service
public class FormulaMedicaService {

    /** Repositorio de fórmulas médicas. */
    private final FormulaMedicaRepository repository;

    /**
     * Construye el servicio.
     *
     * @param repository repositorio de fórmulas.
     */
    public FormulaMedicaService(FormulaMedicaRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista las fórmulas desde la más reciente a la más antigua.
     *
     * @return lista de fórmulas en DTO.
     */
    public List<FormulaMedicaDTO> listar() {
        return repository.findAllByOrderByFechaCreacionRegistroDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Convierte una entidad a un DTO.
     *
     * @param entity entidad origen.
     * @return DTO resultante.
     */
    private FormulaMedicaDTO toDto(FormulaMedica entity) {
        FormulaMedicaDTO dto = new FormulaMedicaDTO();
        dto.setId(entity.getId());
        dto.setCitaId(entity.getCitaId());
        dto.setMedicamentoId(entity.getMedicamentoId());
        dto.setDosis(entity.getDosis());
        dto.setIndicaciones(entity.getIndicaciones());
        dto.setFechaCreacionRegistro(entity.getFechaCreacionRegistro());
        dto.setFechaActualizacionRegistro(entity.getFechaActualizacionRegistro());
        return dto;
    }
}
