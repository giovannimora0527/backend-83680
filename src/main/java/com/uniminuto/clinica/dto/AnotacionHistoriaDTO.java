package com.uniminuto.clinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO para las anotaciones de historia médica.
 */
public class AnotacionHistoriaDTO {

    /** Identificador de la anotación. */
    private Long id;

    /** Identificador de la historia. */
    @NotNull
    private Long historiaId;

    /** Identificador del médico. */
    @NotNull
    private Long medicoId;

    /** Fecha de la anotación. */
    private LocalDateTime fecha;

    /** Descripción de la anotación. */
    @NotBlank
    private String descripcion;

    /** Constructor vacío. */
    public AnotacionHistoriaDTO() {
    }

    /** @return id. */
    public Long getId() { return id; }
    /** @param id id. */
    public void setId(Long id) { this.id = id; }
    /** @return historiaId. */
    public Long getHistoriaId() { return historiaId; }
    /** @param historiaId id de la historia. */
    public void setHistoriaId(Long historiaId) { this.historiaId = historiaId; }
    /** @return medicoId. */
    public Long getMedicoId() { return medicoId; }
    /** @param medicoId id del médico. */
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    /** @return fecha. */
    public LocalDateTime getFecha() { return fecha; }
    /** @param fecha fecha. */
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    /** @return descripción. */
    public String getDescripcion() { return descripcion; }
    /** @param descripcion descripción. */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
