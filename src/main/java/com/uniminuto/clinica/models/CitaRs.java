package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Clase que representa una cita en la respuesta de los servicios.
 */
@Data
public class CitaRs {

    /**
     * Identificador de la cita.
     */
    private Long id;

    /**
     * Identificador del cliente.
     */
    private Long clienteId;

    /**
     * Nombre completo del cliente.
     */
    private String clienteNombre;

    /**
     * Identificador de la mascota.
     */
    private Integer mascotaId;

    /**
     * Nombre de la mascota.
     */
    private String mascotaNombre;

    /**
     * Identificador del medico.
     */
    private Long medicoId;

    /**
     * Nombre completo del medico.
     */
    private String medicoNombre;

    /**
     * Fecha y hora de la cita.
     */
    private LocalDateTime fechaHora;

    /**
     * Estado de la cita.
     */
    private String estado;

    /**
     * Motivo de la consulta.
     */
    private String motivo;
    /** Fecha y hora de fin. */
    private LocalDateTime fechaHoraFin;
    /** Especie de la mascota. */
    private String mascotaEspecie;
    /** Raza de la mascota. */
    private String mascotaRaza;
    /** Edad de la mascota. */
    private Integer mascotaEdad;
    /** Telefono del propietario. */
    private String clienteTelefono;
    /** Documento del propietario. */
    private String clienteDocumento;
    /** Si la cita ya tiene historia clinica (atencion registrada). */
    private Boolean atendidaConHistoria;
}
