package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Historia clínica de una mascota. Cada registro corresponde a una consulta
 * y puede contener múltiples anotaciones y fórmulas médicas.
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    /** Identificador único de la historia clínica. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /** Mascota a la que pertenece la historia. */
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Mascota mascota;

    /** Cliente dueño de la mascota. */
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /** Cita de la que se origina la consulta. */
    @ManyToOne
    @JoinColumn(name = "cita_id")
    private Cita cita;

    /** Veterinario que realizó la consulta. */
    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    /** Fecha y hora de la consulta. */
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    /** Motivo de la consulta. */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /** Diagnóstico de la consulta. */
    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    /** Observaciones generales de la consulta. */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /** Estado de la historia (por ejemplo ABIERTA o CERRADA). */
    @Column(name = "estado", length = 20)
    private String estado;

    /** Fecha en que se creó el registro. */
    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;
}