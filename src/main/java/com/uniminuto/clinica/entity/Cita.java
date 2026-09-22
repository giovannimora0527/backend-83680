package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla cita.
 */
@Entity
@Table(name = "cita")
public class Cita {

    /** Identificador de la cita. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador del cliente. */
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    /** Identificador de la mascota. */
    @Column(name = "mascota_id", nullable = false)
    private Long mascotaId;

    /** Identificador del médico. */
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    /** Fecha y hora de la cita. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Estado de la cita. */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    /** Motivo de la cita. */
    @Column(name = "motivo")
    private String motivo;

    /** Constructor vacío requerido por JPA. */
    public Cita() {
    }

    /** @return identificador de la cita. */
    public Long getId() {
        return id;
    }

    /** @param id identificador de la cita. */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return identificador del cliente. */
    public Long getClienteId() {
        return clienteId;
    }

    /** @param clienteId identificador del cliente. */
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    /** @return identificador de la mascota. */
    public Long getMascotaId() {
        return mascotaId;
    }

    /** @param mascotaId identificador de la mascota. */
    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    /** @return identificador del médico. */
    public Long getMedicoId() {
        return medicoId;
    }

    /** @param medicoId identificador del médico. */
    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    /** @return fecha y hora de la cita. */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /** @param fechaHora fecha y hora de la cita. */
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    /** @return estado de la cita. */
    public String getEstado() {
        return estado;
    }

    /** @param estado estado de la cita. */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /** @return motivo de la cita. */
    public String getMotivo() {
        return motivo;
    }

    /** @param motivo motivo de la cita. */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
