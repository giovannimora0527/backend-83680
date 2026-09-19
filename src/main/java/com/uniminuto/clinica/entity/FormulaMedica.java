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
@Table(name = "formula_medica")
@Data
/** Representa una fórmula médica asociada a una cita. */
public class FormulaMedica {

    /** Identificador de la fórmula. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cita_id", nullable = false)
    /** Identificador de la cita relacionada. */
    private Integer citaId;

    @Column(name = "medicamento_id", nullable = false)
    /** Identificador del medicamento formulado. */
    private Integer medicamentoId;

    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    /** Dosis indicada para el medicamento. */
    private String dosis;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    /** Instrucciones para el paciente. */
    private String indicaciones;

    @Column(name = "fecha_creacion_registro", nullable = false)
    /** Fecha de creación del registro. */
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_actualizacion_registro")
    /** Fecha de la última actualización. */
    private LocalDateTime fechaActualizacionRegistro;
}
    /** Identificador de la cita relacionada. */
    /** Identificador del medicamento formulado. */
    /** Dosis indicada para el medicamento. */
    /** Instrucciones para el paciente. */
    /** Fecha de creación del registro. */
    /** Fecha de la última actualización. */
