package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla anotacion_historia.
 */
@Entity
@Table(name = "anotacion_historia")
public class AnotacionHistoria {

    /** Identificador de la anotación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador de la historia médica. */
    @Column(name = "historia_id", nullable = false)
    private Long historiaId;

    /** Identificador del médico. */
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    /** Fecha de la anotación. */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /** Descripción de la anotación. */
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    /** Constructor vacío requerido por JPA. */
    public AnotacionHistoria() {
    }

    /** @return identificador de la anotación. */
    public Long getId() {
        return id;
    }

    /** @param id identificador de la anotación. */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return identificador de la historia. */
    public Long getHistoriaId() {
        return historiaId;
    }

    /** @param historiaId identificador de la historia. */
    public void setHistoriaId(Long historiaId) {
        this.historiaId = historiaId;
    }

    /** @return identificador del médico. */
    public Long getMedicoId() {
        return medicoId;
    }

    /** @param medicoId identificador del médico. */
    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    /** @return fecha de la anotación. */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /** @param fecha fecha de la anotación. */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /** @return descripción de la anotación. */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param descripcion descripción de la anotación. */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
