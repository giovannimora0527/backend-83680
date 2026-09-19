package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "historia_medica")
@Data
/** Representa la historia médica de un paciente. */
public class HistoriaMedica {

    /** Identificador de la historia médica. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    /** Identificador del paciente. */
    private Integer pacienteId;

    @Column(name = "fecha_creacion")
    /** Fecha de creación de la historia. */
    private LocalDateTime fechaCreacion;
}
    /** Identificador del paciente. */
    /** Fecha de creación de la historia. */
