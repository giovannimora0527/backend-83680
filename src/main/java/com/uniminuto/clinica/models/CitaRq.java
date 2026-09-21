package com.uniminuto.clinica.models;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Clase que representa la solicitud de una cita veterinaria.
 * Se utiliza para crear o actualizar citas en el sistema.
 *
 * @author Alma Hernandez
 */
@Data

public class CitaRq {
    /**
     * Identificador único de la cita.
     * Requerido solo para operaciones de actualización.
     */
    private Long id;

    /**
     * Identificador del cliente propietario de la mascota.
     */
    private Integer clienteId;

    /**
     * Identificador de la mascota a atender.
     */
    private Integer mascotaId;

    /**
     * Identificador del médico veterinario que atiende la cita.
     */
    private Integer medicoId;

    /**
     * Fecha y hora programada para la cita.
     */
    private LocalDateTime fechaHora;

    /**
     * Estado de la cita (ej: programada, completada, cancelada).
     */
    private String estado;

    /**
     * Motivo o descripción de la consulta veterinaria.
     */
    private String motivo;
}