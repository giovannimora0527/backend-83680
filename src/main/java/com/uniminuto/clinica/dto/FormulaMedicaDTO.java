package com.uniminuto.clinica.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferir información de una fórmula médica.
 */
public class FormulaMedicaDTO {

    /** Identificador de la fórmula. */
    private Long id;
    /** Identificador de la cita. */
    private Long citaId;
    /** Identificador del medicamento. */
    private Long medicamentoId;
    /** Dosis prescrita. */
    private String dosis;
    /** Indicaciones prescritas. */
    private String indicaciones;
    /** Fecha de creación. */
    private LocalDateTime fechaCreacionRegistro;
    /** Fecha de actualización. */
    private LocalDateTime fechaActualizacionRegistro;

    /** Constructor vacío. */
    public FormulaMedicaDTO() {
    }

    /** @return id. */
    public Long getId() { return id; }
    /** @param id id. */
    public void setId(Long id) { this.id = id; }
    /** @return id de la cita. */
    public Long getCitaId() { return citaId; }
    /** @param citaId id de la cita. */
    public void setCitaId(Long citaId) { this.citaId = citaId; }
    /** @return id del medicamento. */
    public Long getMedicamentoId() { return medicamentoId; }
    /** @param medicamentoId id del medicamento. */
    public void setMedicamentoId(Long medicamentoId) { this.medicamentoId = medicamentoId; }
    /** @return dosis. */
    public String getDosis() { return dosis; }
    /** @param dosis dosis. */
    public void setDosis(String dosis) { this.dosis = dosis; }
    /** @return indicaciones. */
    public String getIndicaciones() { return indicaciones; }
    /** @param indicaciones indicaciones. */
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }
    /** @return fecha de creación. */
    public LocalDateTime getFechaCreacionRegistro() { return fechaCreacionRegistro; }
    /** @param fechaCreacionRegistro fecha de creación. */
    public void setFechaCreacionRegistro(LocalDateTime fechaCreacionRegistro) { this.fechaCreacionRegistro = fechaCreacionRegistro; }
    /** @return fecha de actualización. */
    public LocalDateTime getFechaActualizacionRegistro() { return fechaActualizacionRegistro; }
    /** @param fechaActualizacionRegistro fecha de actualización. */
    public void setFechaActualizacionRegistro(LocalDateTime fechaActualizacionRegistro) { this.fechaActualizacionRegistro = fechaActualizacionRegistro; }
}
