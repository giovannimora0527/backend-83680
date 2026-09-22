package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa la entidad Fórmula Médica en la base de datos de la clínica veterinaria.
 * Almacena la prescripción de medicamentos, dosis e instrucciones asociadas a una historia clínica[cite: 12].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Entity
@Table(name = "formula_medica")
public class FormulaMedica {

    /** Identificador único de la fórmula médica (Clave primaria autoincrementable)[cite: 12]. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador de la historia clínica o cita asociada a la fórmula médica[cite: 12]. */
    @Column(name = "cita_id", nullable = false)
    private Integer historiaId;

    /** Identificador del medicamento prescrito en la fórmula[cite: 12]. */
    @Column(name = "medicamento_id", nullable = false)
    private Integer medicamentoId;

    /** Dosis exacta que se debe administrar del medicamento[cite: 12]. */
    @Column(name = "dosis", nullable = false)
    private String dosis;

    /** Instrucciones o recomendaciones especiales para la administración del medicamento[cite: 12]. */
    @Column(name = "indicaciones")
    private String instrucciones;

    /** Fecha y hora en la que se creó el registro de la fórmula médica[cite: 12]. */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha y hora de la última actualización realizada al registro de la fórmula médica[cite: 12]. */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;

    /**
     * Constructor por defecto de la clase FormulaMedica[cite: 12].
     */
    public FormulaMedica() {
    }

    /**
     * Constructor con todos los parámetros para inicializar una fórmula médica[cite: 12].
     *
     * @param id Identificador único de la fórmula[cite: 12].
     * @param historiaId Identificador de la historia clínica[cite: 12].
     * @param medicamentoId Identificador del medicamento[cite: 12].
     * @param dosis Dosis del medicamento[cite: 12].
     * @param instrucciones Instrucciones de consumo[cite: 12].
     * @param fechaCreacionRegistro Fecha de creación del registro[cite: 12].
     * @param fechaActualizacionRegistro Fecha de actualización del registro[cite: 12].
     */
    public FormulaMedica(Long id, Integer historiaId, Integer medicamentoId, String dosis, String instrucciones, LocalDateTime fechaCreacionRegistro, LocalDateTime fechaActualizacionRegistro) {
        this.id = id;
        this.historiaId = historiaId;
        this.medicamentoId = medicamentoId;
        this.dosis = dosis;
        this.instrucciones = instrucciones;
        this.fechaCreacionRegistro = fechaCreacionRegistro;
        this.fechaActualizacionRegistro = fechaActualizacionRegistro;
    }

    /**
     * Obtiene el identificador único de la fórmula médica[cite: 12].
     * @return El identificador de la fórmula[cite: 12].
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la fórmula médica[cite: 12].
     * @param id El identificador a asignar[cite: 12].
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador de la historia clínica asociada[cite: 12].
     * @return El identificador de la historia[cite: 12].
     */
    public Integer getHistoriaId() {
        return historiaId;
    }

    /**
     * Establece el identificador de la historia clínica asociada[cite: 12].
     * @param historiaId El identificador de la historia a asignar[cite: 12].
     */
    public void setHistoriaId(Integer historiaId) {
        this.historiaId = historiaId;
    }

    /**
     * Obtiene el identificador del medicamento prescrito[cite: 12].
     * @return El identificador del medicamento[cite: 12].
     */
    public Integer getMedicamentoId() {
        return medicamentoId;
    }

    /**
     * Establece el identificador del medicamento prescrito[cite: 12].
     * @param medicamentoId El identificador del medicamento a asignar[cite: 12].
     */
    public void setMedicamentoId(Integer medicamentoId) {
        this.medicamentoId = medicamentoId;
    }

    /**
     * Obtiene la dosis del medicamento[cite: 12].
     * @return La dosis prescrita[cite: 12].
     */
    public String getDosis() {
        return dosis;
    }

    /**
     * Establece la dosis del medicamento[cite: 12].
     * @param dosis La dosis a asignar[cite: 12].
     */
    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    /**
     * Obtiene las instrucciones de administración del medicamento[cite: 12].
     * @return Las instrucciones detalladas[cite: 12].
     */
    public String getInstrucciones() {
        return instrucciones;
    }

    /**
     * Establece las instrucciones de administración del medicamento[cite: 12].
     * @param instrucciones Las instrucciones a asignar[cite: 12].
     */
    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }

    /**
     * Obtiene la fecha de creación del registro[cite: 12].
     * @return La fecha de creación[cite: 12].
     */
    public LocalDateTime getFechaCreacionRegistro() {
        return fechaCreacionRegistro;
    }

    /**
     * Establece la fecha de creación del registro[cite: 12].
     * @param fechaCreacionRegistro La fecha de creación a asignar[cite: 12].
     */
    public void setFechaCreacionRegistro(LocalDateTime fechaCreacionRegistro) {
        this.fechaCreacionRegistro = fechaCreacionRegistro;
    }

    /**
     * Obtiene la fecha de la última actualización del registro[cite: 12].
     * @return La fecha de actualización[cite: 12].
     */
    public LocalDateTime getFechaActualizacionRegistro() {
        return fechaActualizacionRegistro;
    }

    /**
     * Establece la fecha de la última actualización del registro[cite: 12].
     * @param fechaActualizacionRegistro La fecha de actualización a asignar[cite: 12].
     */
    public void setFechaActualizacionRegistro(LocalDateTime fechaActualizacionRegistro) {
        this.fechaActualizacionRegistro = fechaActualizacionRegistro;
    }
}