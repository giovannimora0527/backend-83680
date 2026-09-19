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
 * Representa una anotación registrada en una historia médica.
 */
@Entity
@Table(name = "anotacion_historia")
@Data
public class AnotacionHistoria {

    /**
     * Identificador único de la anotación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador de la historia médica asociada.
     */
    @Column(name = "historia_id")
    private Integer historiaId;

    /**
     * Identificador del médico que realiza la anotación.
     */
    @Column(name = "medico_id")
    private Integer medicoId;

    /**
     * Fecha en la que se registra la anotación.
     */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /**
     * Descripción o contenido de la anotación.
     */
    @Column(name = "descripcion")
    private String descripcion;
}