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

@Entity
@Table(name = "bloqueo_horario")
@Data
/** Representa un bloqueo de horario en la agenda de un veterinario. */
public class BloqueoHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    /** Identificador del bloqueo. */
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    /** Veterinario al que aplica el bloqueo de horario. */
    private Medico medico;

    @Column(name = "fecha_hora_inicio", nullable = false)
    /** Fecha y hora en que inicia el bloqueo. */
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    /** Fecha y hora en que termina el bloqueo. */
    private LocalDateTime fechaHoraFin;

    @Column(name = "motivo", nullable = false, length = 250)
    /** Motivo del bloqueo (ej. cirugía, descanso, capacitación). */
    private String motivo;

    @Column(name = "activo", nullable = false)
    /** Indica si el bloqueo se encuentra vigente. */
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    /** Fecha y hora de creación del bloqueo. */
    private LocalDateTime fechaCreacion;
}
