package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalTime;

/**
 * Horario de disponibilidad de un veterinario para un dia de la semana.
 */
@Entity
@Data
@Table(name = "horario_medico")
public class HorarioMedico {

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Medico. */
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    /** Dia de la semana: 1 = lunes ... 7 = domingo. */
    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;

    /** Hora de inicio. */
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    /** Hora de fin. */
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /** Si esta activo. */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
