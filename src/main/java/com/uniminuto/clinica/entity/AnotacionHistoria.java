package com.uniminuto.clinica.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Clase que representa una anotación realizada en una historia médica.
 */
@Data
@Entity
@Table(name = "anotacion_historia")
public class AnotacionHistoria implements Serializable {

    /**
     * Identificador utilizado para la serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador de la anotación.
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
     * Identificador del médico que realizó la anotación.
     */
    @Column(name = "medico_id")
    private Integer medicoId;

    /**
     * Fecha en la que se realizó la anotación.
     */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /**
     * Descripción de la anotación médica.
     */
    @Column(name = "descripcion")
    private String descripcion;
}