package com.uniminuto.clinica.service;

import com.uniminuto.clinica.dto.AnotacionHistoriaDTO;
import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.RecursoNoEncontradoException;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Lógica de negocio de las anotaciones de historia médica.
 */
@Service
public class AnotacionHistoriaService {

    /** Repositorio de anotaciones. */
    private final AnotacionHistoriaRepository repository;

    /**
     * Construye el servicio.
     *
     * @param repository repositorio de anotaciones.
     */
    public AnotacionHistoriaService(AnotacionHistoriaRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todas las anotaciones ordenadas descendentemente por fecha.
     *
     * @return lista de anotaciones.
     */
    public List<AnotacionHistoriaDTO> listar() {
        return repository.findAllByOrderByFechaDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Lista anotaciones dentro de un rango de fechas.
     *
     * @param fechaInicial fecha inicial inclusive.
     * @param fechaFinal fecha final inclusive.
     * @return anotaciones ordenadas de la más reciente a la más antigua.
     */
    public List<AnotacionHistoriaDTO> listarPorRango(
            LocalDate fechaInicial,
            LocalDate fechaFinal) {

        if (fechaInicial == null || fechaFinal == null) {
            throw new IllegalArgumentException("Las dos fechas son obligatorias.");
        }
        if (fechaInicial.isAfter(fechaFinal)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser posterior a la fecha final."
            );
        }

        LocalDateTime inicio = fechaInicial.atStartOfDay();
        LocalDateTime fin = fechaFinal.atTime(LocalTime.MAX);

        return repository.findByFechaBetweenOrderByFechaDesc(inicio, fin)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Crea una anotación de historia.
     *
     * @param dto datos de la anotación.
     * @return anotación creada.
     */
    public AnotacionHistoriaDTO crear(AnotacionHistoriaDTO dto) {
        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoriaId(dto.getHistoriaId());
        anotacion.setMedicoId(dto.getMedicoId());
        anotacion.setFecha(
                dto.getFecha() == null ? LocalDateTime.now() : dto.getFecha()
        );
        anotacion.setDescripcion(dto.getDescripcion());

        return toDto(repository.save(anotacion));
    }

    /**
     * Actualiza una anotación existente.
     *
     * @param id identificador de la anotación.
     * @param dto nuevos datos.
     * @return anotación actualizada.
     */
    public AnotacionHistoriaDTO actualizar(
            Long id,
            AnotacionHistoriaDTO dto) {

        AnotacionHistoria anotacion = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la anotación con id " + id
                        )
                );

        anotacion.setHistoriaId(dto.getHistoriaId());
        anotacion.setMedicoId(dto.getMedicoId());
        anotacion.setFecha(
                dto.getFecha() == null
                        ? anotacion.getFecha()
                        : dto.getFecha()
        );
        anotacion.setDescripcion(dto.getDescripcion());

        return toDto(repository.save(anotacion));
    }

    /**
     * Convierte una entidad en DTO.
     *
     * @param entity entidad de anotación.
     * @return DTO de anotación.
     */
    private AnotacionHistoriaDTO toDto(AnotacionHistoria entity) {
        AnotacionHistoriaDTO dto = new AnotacionHistoriaDTO();
        dto.setId(entity.getId());
        dto.setHistoriaId(entity.getHistoriaId());
        dto.setMedicoId(entity.getMedicoId());
        dto.setFecha(entity.getFecha());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
