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
@Table(name = "historia_medica")
@Data
/** Representa la historia clínica de una consulta realizada a una mascota. */
public class HistoriaMedica {

    /** Identificador de la historia médica. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    /** Mascota (paciente) a la que pertenece la historia. */
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    /** Cliente propietario de la mascota. */
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "cita_id")
    /** Cita atendida que generó la historia. */
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    /** Veterinario que realizó la consulta. */
    private Medico medico;

    @Column(name = "fecha_hora")
    /** Fecha y hora de la consulta. */
    private LocalDateTime fechaHora;

    @Column(name = "motivo", columnDefinition = "TEXT")
    /** Motivo de la consulta. */
    private String motivo;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    /** Diagnóstico dado por el veterinario. */
    private String diagnostico;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    /** Observaciones generales de la consulta. */
    private String observaciones;

    @Column(name = "estado", length = 20)
    /** Estado de la historia: ABIERTA o CERRADA. */
    private String estado;

    @Column(name = "fecha_creacion")
    /** Fecha de creación de la historia. */
    private LocalDateTime fechaCreacion;
}
