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

import java.time.LocalDate;
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

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    /** Cita (consulta) en la que se generó la fórmula. */
    private Cita cita;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    /** Mascota a la que se le formula el medicamento. */
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    /** Veterinario que emite la fórmula. */
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    /** Medicamento formulado. */
    private Medicamento medicamento;

    @Column(name = "fecha_emision")
    /** Fecha en que se emite la fórmula. */
    private LocalDate fechaEmision;

    @Column(name = "cantidad_solicitada", nullable = false)
    /** Cantidad solicitada en la fórmula. */
    private Integer cantidadSolicitada = 0;

    @Column(name = "cantidad_entregada", nullable = false)
    /** Cantidad entregada a la fecha. */
    private Integer cantidadEntregada = 0;

    @Column(name = "estado", nullable = false, length = 30)
    /** Estado de la formulación/dispensación. */
    private String estado = "FORMULADA";

    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    /** Dosis indicada para el medicamento. */
    private String dosis;

    @Column(name = "frecuencia", length = 100)
    /** Cada cuánto se debe dar el medicamento. */
    private String frecuencia;

    @Column(name = "duracion_dias")
    /** Duración del tratamiento en días. */
    private Integer duracionDias;

    @Column(name = "indicaciones", columnDefinition = "TEXT")
    /** Instrucciones para el paciente. */
    private String indicaciones;

    @Column(name = "activo", nullable = false)
    /** Indica si la fórmula está activa; las fórmulas no se eliminan, se inactivan. */
    private Boolean activo;

    @Column(name = "fecha_dispensacion")
    /** Fecha en la que se entregó la última cantidad. */
    private LocalDateTime fechaDispenacion;

    @Column(name = "fecha_creacion_registro", nullable = false)
    /** Fecha de creación del registro. */
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_actualizacion_registro")
    /** Fecha de la última actualización. */
    private LocalDateTime fechaActualizacionRegistro;
}
