package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Anotación realizada por un veterinario dentro de una historia clínica.
 */
@Entity
@Table(name = "anotacion_historia")
@Data
public class AnotacionHistoria {

    /** Identificador único de la anotación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Historia clínica a la que pertenece la anotación. */
    @JoinColumn(name = "historia_id", nullable = false)
    @ManyToOne
    private HistoriaMedica historia;

    /** Veterinario que escribió la anotación. */
    @JoinColumn(name = "medico_id", nullable = false)
    @ManyToOne
    private Medico medico;

    /** Fecha y hora de la anotación. */
    @Column(name = "fecha", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fecha;

    /** Descripción de lo ocurrido. */
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    /** Observación adicional del veterinario. */
    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    /** Diagnóstico o evolución del paciente. */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;
}