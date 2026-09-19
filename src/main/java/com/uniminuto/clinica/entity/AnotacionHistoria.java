package com.uniminuto.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "anotacion_historia")
@Data
/** Representa una anotación escrita en una historia médica. */
public class AnotacionHistoria {

    /** Identificador de la anotación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    /** Historia médica a la que pertenece la anotación. */
    private HistoriaMedica historiaMedica;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    /** Médico que registra la anotación. */
    private Medico medico;

    @Column(name = "fecha")
    /** Fecha y hora en que se registra la anotación. */
    private LocalDateTime fecha;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    /** Texto descriptivo de la anotación. */
    private String descripcion;
}
