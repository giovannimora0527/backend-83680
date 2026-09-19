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

import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
@Data
/** Representa una cita veterinaria. */
public class Cita {

    /** Identificador de la cita. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    /** Cliente que solicita la cita. */
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "mascota_id", nullable = false)
    /** Mascota atendida en la cita. */
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "medico_id", nullable = false)
    /** Médico encargado de la atención. */
    private Medico medico;

    @Column(name = "fecha_hora", nullable = false)
    /** Fecha y hora programada. */
    private LocalDateTime fechaHora;

    @Column(name = "estado", nullable = false, length = 20)
    /** Estado actual de la cita. */
    private String estado;

    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    /** Motivo de la consulta. */
    private String motivo;

}
    /** Cliente que solicita la cita. */
    /** Mascota atendida en la cita. */
    /** Médico encargado de la atención. */
    /** Fecha y hora programada. */
    /** Estado actual de la cita. */
    /** Motivo de la consulta. */
