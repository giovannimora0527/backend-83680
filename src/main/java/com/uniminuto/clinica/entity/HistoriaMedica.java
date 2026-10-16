package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad de la tabla historia_medica.
 * Cada historia es una consulta de una mascota y puede tener varias anotaciones.
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    /** Id de la historia. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Mascota (paciente) a la que pertenece la historia. La columna se llama paciente_id. */
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Mascota mascota;

    /** Cita (consulta) de la que sale la historia. */
    @ManyToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;

    /** Veterinario que atendio. */
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    /** Fecha y hora de la consulta. */
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /** Diagnostico del veterinario. */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    /** Observaciones generales. */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /** Fecha en que se creo el registro. */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
