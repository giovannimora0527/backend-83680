package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla formula_medica.
 */
@Entity
@Table(name = "formula_medica")
public class FormulaMedica {

    /** Identificador de la fórmula médica. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador de la cita asociada. */
    @Column(name = "cita_id", nullable = false)
    private Long citaId;

    /** Identificador del medicamento asociado. */
    @Column(name = "medicamento_id", nullable = false)
    private Long medicamentoId;

    /** Dosis indicada en la fórmula. */
    @Column(name = "dosis", nullable = false)
    private String dosis;

    /** Indicaciones de uso. */
    @Column(name = "indicaciones")
    private String indicaciones;

    /** Fecha de creación del registro. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha de actualización del registro. */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;

    /** Constructor vacío requerido por JPA. */
    public FormulaMedica() {
    }

    /** @return identificador de la fórmula. */
    public Long getId() {
        return id;
    }

    /** @param id identificador de la fórmula. */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return identificador de la cita. */
    public Long getCitaId() {
        return citaId;
    }

    /** @param citaId identificador de la cita. */
    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    /** @return identificador del medicamento. */
    public Long getMedicamentoId() {
        return medicamentoId;
    }

    /** @param medicamentoId identificador del medicamento. */
    public void setMedicamentoId(Long medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    /** @return dosis indicada. */
    public String getDosis() {
        return dosis;
    }

    /** @param dosis dosis indicada. */
    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    /** @return indicaciones de uso. */
    public String getIndicaciones() {
        return indicaciones;
    }

    /** @param indicaciones indicaciones de uso. */
    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    /** @return fecha de creación. */
    public LocalDateTime getFechaCreacionRegistro() {
        return fechaCreacionRegistro;
    }

    /** @param fechaCreacionRegistro fecha de creación. */
    public void setFechaCreacionRegistro(LocalDateTime fechaCreacionRegistro) {
        this.fechaCreacionRegistro = fechaCreacionRegistro;
    }

    /** @return fecha de actualización. */
    public LocalDateTime getFechaActualizacionRegistro() {
        return fechaActualizacionRegistro;
    }

    /** @param fechaActualizacionRegistro fecha de actualización. */
    public void setFechaActualizacionRegistro(LocalDateTime fechaActualizacionRegistro) {
        this.fechaActualizacionRegistro = fechaActualizacionRegistro;
    }
}
