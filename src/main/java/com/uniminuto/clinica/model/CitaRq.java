package com.uniminuto.clinica.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Objeto de solicitud para crear o actualizar una cita.
 */
@Data
public class CitaRq {

    /**
     * Identificador de la cita.
     */
    private Integer id;

    /**
    * Identificador del cliente asociado a la cita.
    */
    @NotNull(message = "El identificador del cliente es obligatorio.")
    private Integer clienteId;

    /**
 * Identificador de la mascota asociada a la cita.
 */
    @NotNull(message = "El identificador de la mascota es obligatorio.")
    private Integer mascotaId;

    /**
     * Identificador del médico que atenderá la cita.
     */
    @NotNull(message = "El identificador del médico es obligatorio.")
    private Integer medicoId;

    /**
     * Fecha y hora programada para la cita.
     */
    @NotBlank(message = "La fecha y hora de la cita es obligatoria.")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
        message = "La fecha y hora debe tener el formato yyyy-MM-dd HH:mm:ss."
    )
    private String fechaHora;

    /**
     * Estado actual de la cita.
     */
    @NotBlank(message = "El estado de la cita es obligatorio.")
    private String estado;

    /**
     * Motivo de la cita.
     */
    @NotBlank(message = "El motivo de la cita es obligatorio.")
    private String motivo;
}