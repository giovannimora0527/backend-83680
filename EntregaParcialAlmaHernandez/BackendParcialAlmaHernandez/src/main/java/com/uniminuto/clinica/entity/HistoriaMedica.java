package com.uniminuto.clinica.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que representa una historia médica de la clínica veterinaria.
 */
@Data
@Entity
@Table(name = "historia_medica")
public class HistoriaMedica implements Serializable {

    /**
     * Identificador utilizado para la serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador de la historia médica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador del paciente asociado a la historia médica.
     */
    @Column(name = "paciente_id")
    private Integer pacienteId;

    /**
     * Fecha en la que se creó la historia médica.
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /**
     * Lista de anotaciones relacionadas con la historia médica.
     */
    @OneToMany
    @JoinColumn(
            name = "historia_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false
    )
    private List<AnotacionHistoria> anotaciones;
}