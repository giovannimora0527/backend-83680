package com.uniminuto.clinica.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Representa una fórmula médica registrada en la clínica.
 */
@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    /**
     * Identificador único de la fórmula médica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador de la cita asociada a la fórmula.
     */
    @Column(name = "cita_id")
    private Integer citaId;

    /**
     * Identificador del medicamento asociado a la fórmula.
     */
    @Column(name = "medicamento_id")
    private Integer medicamentoId;

    /**
     * Dosis indicada para el medicamento.
     */
    @Column(name = "dosis")
    private String dosis;

    /**
     * Indicaciones para el uso del medicamento.
     */
    @Column(name = "indicaciones")
    private String indicaciones;

    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_creacion_registro")
    private LocalDateTime fechaCreacionRegistro;

    /**
     * Fecha de actualización del registro.
     */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;
}