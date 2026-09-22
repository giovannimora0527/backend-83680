package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa la entidad Cita médica en el sistema de la clínica veterinaria.
 * Almacena la programación de citas entre clientes, mascotas y médicos veterinarios[cite: 11].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Entity
@Table(name = "cita")
public class Cita {

    /** Identificador único de la cita (Clave primaria autoincrementable)[cite: 11]. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador del cliente propietario de la mascota que asiste a la cita[cite: 11]. */
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    /** Identificador de la mascota que recibe la atención médica[cite: 11]. */
    @Column(name = "mascota_id", nullable = false)
    private Long mascotaId;

    /** Identificador del médico veterinario asignado para la cita[cite: 11]. */
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    /** Fecha y hora programada para la realización de la cita[cite: 11]. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Motivo o razón de la consulta médica veterinaria[cite: 11]. */
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    /** Estado actual en el que se encuentra la cita (ej. programada, atendida, cancelada)[cite: 11]. */
    @Column(name = "estado", length = 50)
    private String estado;

    /**
     * Constructor por defecto de la clase Cita[cite: 11].
     */
    public Cita() {
    }

    /**
     * Constructor con todos los parámetros para inicializar una cita médica[cite: 11].
     *
     * @param id Identificador único de la cita[cite: 11].
     * @param clienteId Identificador del cliente[cite: 11].
     * @param mascotaId Identificador de la mascota[cite: 11].
     * @param medicoId Identificador del médico[cite: 11].
     * @param fechaHora Fecha y hora de la cita[cite: 11].
     * @param motivo Motivo de la consulta[cite: 11].
     * @param estado Estado actual de la cita[cite: 11].
     */
    public Cita(Long id, Long clienteId, Long mascotaId, Long medicoId, LocalDateTime fechaHora, String motivo, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.mascotaId = mascotaId;
        this.medicoId = medicoId;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    /**
     * Obtiene el identificador único de la cita[cite: 11].
     * @return El identificador de la cita[cite: 11].
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la cita[cite: 11].
     * @param id El identificador a asignar[cite: 11].
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador del cliente[cite: 11].
     * @return El identificador del cliente[cite: 11].
     */
    public Long getClienteId() {
        return clienteId;
    }

    /**
     * Establece el identificador del cliente[cite: 11].
     * @param clienteId El identificador del cliente a asignar[cite: 11].
     */
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    /**
     * Obtiene el identificador de la mascota[cite: 11].
     * @return El identificador de la mascota[cite: 11].
     */
    public Long getMascotaId() {
        return mascotaId;
    }

    /**
     * Establece el identificador de la mascota[cite: 11].
     * @param mascotaId El identificador de la mascota a asignar[cite: 11].
     */
    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    /**
     * Obtiene el identificador del médico veterinario[cite: 11].
     * @return El identificador del médico[cite: 11].
     */
    public Long getMedicoId() {
        return medicoId;
    }

    /**
     * Establece el identificador del médico veterinario[cite: 11].
     * @param medicoId El identificador del médico a asignar[cite: 11].
     */
    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    /**
     * Obtiene la fecha y hora programada de la cita[cite: 11].
     * @return La fecha y hora de la cita[cite: 11].
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /**
     * Establece la fecha y hora programada de la cita[cite: 11].
     * @param fechaHora La fecha y hora a asignar[cite: 11].
     */
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    /**
     * Obtiene el motivo de la consulta de la cita[cite: 11].
     * @return El motivo de la consulta[cite: 11].
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Establece el motivo de la consulta de la cita[cite: 11].
     * @param motivo El motivo a asignar[cite: 11].
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Obtiene el estado actual de la cita[cite: 11].
     * @return El estado de la cita[cite: 11].
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual de la cita[cite: 11].
     * @param estado El estado a asignar[cite: 11].
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
}