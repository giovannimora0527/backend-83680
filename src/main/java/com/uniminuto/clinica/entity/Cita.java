package com.uniminuto.clinica.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Clase que mapea la tabla cita en la base de datos.
 */
@Data
@Entity
@Table(name = "cita")
public class Cita implements Serializable {

    /**
     * Identificador utilizado para la serialización.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador de la cita.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador del cliente asociado a la cita.
     */
    @Column(name = "cliente_id")
    private Integer clienteId;

    /**
     * Identificador de la mascota asociada a la cita.
     */
    @Column(name = "mascota_id")
    private Integer mascotaId;

    /**
     * Médico asociado a la cita.
     */
    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Medico medico;

    /**
     * Fecha y hora programada para la cita.
     */
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    /**
     * Estado actual de la cita.
     */
    @Column(name = "estado")
    private String estado;

    /**
     * Motivo por el cual se solicita la cita.
     */
    @Column(name = "motivo")
    private String motivo;
}