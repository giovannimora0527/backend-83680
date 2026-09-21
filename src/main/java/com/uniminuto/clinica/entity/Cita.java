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
 * Entidad que representa una cita veterinaria en el sistema.
 * Esta entidad es fundamental para los requerimientos de filtrado por rango de fechas,
 * ordenamiento descendente y operaciones de creación/actualización (CRUD).
 *
 * @author Alma Hernandez
 */

@Entity
@Data
@Table(name = "cita")

public class Cita {
    /**
     * Identificador único y clave primaria de la cita.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * Identificador de la persona dueña de la mascota asociada a esta cita.
     * (Puede ser un Long o una relación @ManyToOne con la entidad Mascota).
     */
    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    /**
     * Identificador de la mascota asociada a esta cita.
     * (Puede ser un Long o una relación @ManyToOne con la entidad Mascota).
     */
    @Column(name = "mascota_id", nullable = false)
    private Integer mascotaId;

    /**
     * Identificador del médico veterinario que atiende la cita.
     */
    @Column(name = "medico_id", nullable = false)
    private Integer medicoId;

    /**
     * Fecha y hora programada para la cita.
     * Este campo es CRÍTICO para cumplir el requerimiento de filtrar 
     * entre una fecha inicial y final, y ordenar de más reciente a más antigua.
     */
    @Column(name = "fecha_cita", nullable = false)
    private LocalDateTime fechaCita;

    /**
     * Estado actual de la cita (ej: "PROGRAMADA", "COMPLETADA", "CANCELADA").
     */
    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    /**
     * Motivo o descripción de la consulta veterinaria.
     */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;
}