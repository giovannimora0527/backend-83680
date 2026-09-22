package com.uniminuto.clinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO para crear, consultar y actualizar citas.
 */
public class CitaDTO {

    /** Identificador de la cita. */
    private Long id;

    /** Identificador del cliente. */
    @NotNull
    private Long clienteId;

    /** Identificador de la mascota. */
    @NotNull
    private Long mascotaId;

    /** Identificador del médico. */
    @NotNull
    private Long medicoId;

    /** Fecha y hora de la cita. */
    @NotNull
    private LocalDateTime fechaHora;

    /** Estado de la cita. */
    @NotBlank
    private String estado;

    /** Motivo de la cita. */
    private String motivo;

    /** Constructor vacío. */
    public CitaDTO() {
    }

    /** @return id. */
    public Long getId() { return id; }
    /** @param id id. */
    public void setId(Long id) { this.id = id; }
    /** @return clienteId. */
    public Long getClienteId() { return clienteId; }
    /** @param clienteId id del cliente. */
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    /** @return mascotaId. */
    public Long getMascotaId() { return mascotaId; }
    /** @param mascotaId id de la mascota. */
    public void setMascotaId(Long mascotaId) { this.mascotaId = mascotaId; }
    /** @return medicoId. */
    public Long getMedicoId() { return medicoId; }
    /** @param medicoId id del médico. */
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    /** @return fecha y hora. */
    public LocalDateTime getFechaHora() { return fechaHora; }
    /** @param fechaHora fecha y hora. */
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    /** @return estado. */
    public String getEstado() { return estado; }
    /** @param estado estado. */
    public void setEstado(String estado) { this.estado = estado; }
    /** @return motivo. */
    public String getMotivo() { return motivo; }
    /** @param motivo motivo. */
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
