package com.uniminuto.clinica.service;

import com.uniminuto.clinica.dto.CitaDTO;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.RecursoNoEncontradoException;
import com.uniminuto.clinica.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Lógica de negocio de las citas.
 */
@Service
public class CitaService {

    /** Repositorio de citas. */
    private final CitaRepository repository;

    /**
     * Construye el servicio.
     *
     * @param repository repositorio de citas.
     */
    public CitaService(CitaRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista citas dentro de un rango de fechas.
     *
     * @param fechaInicial fecha inicial inclusive.
     * @param fechaFinal fecha final inclusive.
     * @return citas ordenadas de la más reciente a la más antigua.
     */
    public List<CitaDTO> listarPorRango(LocalDate fechaInicial, LocalDate fechaFinal) {
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

        return repository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Crea una nueva cita.
     *
     * @param dto datos de la cita.
     * @return cita creada.
     */
    public CitaDTO crear(CitaDTO dto) {
        Cita cita = new Cita();
        cita.setClienteId(dto.getClienteId());
        cita.setMascotaId(dto.getMascotaId());
        cita.setMedicoId(dto.getMedicoId());
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado(dto.getEstado());
        cita.setMotivo(dto.getMotivo());

        return toDto(repository.save(cita));
    }

    /**
     * Actualiza una cita existente.
     *
     * @param id identificador de la cita.
     * @param dto nuevos datos.
     * @return cita actualizada.
     */
    public CitaDTO actualizar(Long id, CitaDTO dto) {
        Cita cita = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No existe la cita con id " + id
                        )
                );

        cita.setClienteId(dto.getClienteId());
        cita.setMascotaId(dto.getMascotaId());
        cita.setMedicoId(dto.getMedicoId());
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado(dto.getEstado());
        cita.setMotivo(dto.getMotivo());

        return toDto(repository.save(cita));
    }

    /**
     * Convierte una entidad a un DTO.
     *
     * @param entity entidad de cita.
     * @return DTO de cita.
     */
    private CitaDTO toDto(Cita entity) {
        CitaDTO dto = new CitaDTO();
        dto.setId(entity.getId());
        dto.setClienteId(entity.getClienteId());
        dto.setMascotaId(entity.getMascotaId());
        dto.setMedicoId(entity.getMedicoId());
        dto.setFechaHora(entity.getFechaHora());
        dto.setEstado(entity.getEstado());
        dto.setMotivo(entity.getMotivo());
        return dto;
    }
}
