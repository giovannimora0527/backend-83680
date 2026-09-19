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
 * Representa una cita registrada en la clínica.
 */
@Entity
@Table(name = "cita")
@Data
public class Cita {

    /**
     * Identificador único de la cita.
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
     * Identificador del médico asociado a la cita.
     */
    @Column(name = "medico_id")
    private Integer medicoId;

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
     * Motivo de la cita.
     */
    @Column(name = "motivo")
    private String motivo;
}