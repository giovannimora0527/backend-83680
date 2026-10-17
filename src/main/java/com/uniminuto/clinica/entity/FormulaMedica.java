package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Fórmula médica emitida por un veterinario para una mascota.
 * No se elimina físicamente: se inactiva con el campo {@code activo}.
 */
@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    /** Identificador único de la fórmula. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Cita o consulta asociada. */
    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /** Historia clínica a la que pertenece la fórmula. */
    @ManyToOne
    @JoinColumn(name = "historia_id")
    private HistoriaMedica historia;

    /** Mascota a la que se le formula. */
    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    /** Veterinario que emite la fórmula. */
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    /** Medicamento formulado. */
    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    /** Dosis del medicamento (por ejemplo "250 mg"). */
    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;

    /** Frecuencia de administración (por ejemplo "cada 12 horas"). */
    @Column(name = "frecuencia", length = 100)
    private String frecuencia;

    /** Duración del tratamiento en días. */
    @Column(name = "duracion_dias")
    private Integer duracionDias;

    /** Indicaciones adicionales. */
    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    /** Fecha de emisión de la fórmula. */
    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    /** Indica si la fórmula sigue activa (false = inactivada). */
    @Column(name = "activo", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    private Boolean activo = true;

    /** Fecha de creación del registro. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha de la última actualización del registro. */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;
}