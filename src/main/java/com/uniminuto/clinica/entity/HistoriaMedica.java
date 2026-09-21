package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa una historia médica, a la que se le agregan anotaciones
 * (ver {@link AnotacionHistoria}).
 */
@Entity
@Table(name = "historia_medica")
@Data
public class HistoriaMedica {

    /**
     * Identificador único de la historia médica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador del paciente al que pertenece la historia.
     */
    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;

    /**
     * Fecha y hora en que se creó la historia.
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
