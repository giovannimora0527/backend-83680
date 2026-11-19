package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
/** Datos recibidos para crear o actualizar una cita. */
public class CitaRq {
    /** Identificador de la cita cuando se actualiza. */
    private Long citaId;
    /** Identificador del cliente. */
    private Long clienteId;
    /** Identificador de la mascota. */
    private Integer mascotaId;
    /** Identificador del médico. */
    private Long medicoId;
    /** Fecha y hora de inicio de la cita. */
    private LocalDateTime fechaHora;
    /** Fecha y hora de fin de la cita (opcional, por defecto +30 min). */
    private LocalDateTime fechaHoraFin;
    /** Estado de la cita. */
    private String estado;
    /** Motivo de la consulta. */
    private String motivo;
    /** Perfil o usuario que realiza la operación para trazabilidad. */
    private String usuarioCambio;
}
