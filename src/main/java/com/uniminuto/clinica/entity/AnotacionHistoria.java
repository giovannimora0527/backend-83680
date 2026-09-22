package com.uniminuto.clinica.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa la entidad de Anotación de Historia en la base de datos de la clínica veterinaria.
 * Almacena los registros, notas y observaciones realizadas por los médicos sobre las historias clínicas[cite: 10].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Entity
@Table(name = "anotacion_historia")
public class AnotacionHistoria {

    /** Identificador único de la anotación de la historia (Clave primaria autoincrementable)[cite: 10]. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador de la historia clínica asociada a la anotación[cite: 10]. */
    @Column(name = "historia_id", nullable = false)
    private Integer historiaId;

    /** Identificador del médico responsable de realizar la anotación[cite: 10]. */
    @Column(name = "medico_id", nullable = false)
    private Integer medicoId;

    /** Fecha y hora en la que se registra la anotación médica[cite: 10]. */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /** Descripción detallada u observaciones consignadas en la anotación[cite: 10]. */
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    /**
     * Constructor por defecto de la clase AnotacionHistoria[cite: 10].
     */
    public AnotacionHistoria() {
    }

    /**
     * Constructor con todos los parámetros para inicializar una anotación de historia[cite: 10].
     *
     * @param id Identificador único de la anotación[cite: 10].
     * @param historiaId Identificador de la historia clínica[cite: 10].
     * @param medicoId Identificador del médico[cite: 10].
     * @param fecha Fecha y hora del registro[cite: 10].
     * @param descripcion Descripción u observaciones[cite: 10].
     */
    public AnotacionHistoria(Long id, Integer historiaId, Integer medicoId, LocalDateTime fecha, String descripcion) {
        this.id = id;
        this.historiaId = historiaId;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el identificador de la anotación[cite: 10].
     * @return El identificador único[cite: 10].
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador de la anotación[cite: 10].
     * @param id El identificador a asignar[cite: 10].
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador de la historia clínica[cite: 10].
     * @return El identificador de la historia[cite: 10].
     */
    public Integer getHistoriaId() {
        return historiaId;
    }

    /**
     * Establece el identificador de la historia clínica[cite: 10].
     * @param historiaId El identificador de la historia a asignar[cite: 10].
     */
    public void setHistoriaId(Integer historiaId) {
        this.historiaId = historiaId;
    }

    /**
     * Obtiene el identificador del médico[cite: 10].
     * @return El identificador del médico[cite: 10].
     */
    public Integer getMedicoId() {
        return medicoId;
    }

    /**
     * Establece el identificador del médico[cite: 10].
     * @param medicoId El identificador del médico a asignar[cite: 10].
     */
    public void setMedicoId(Integer medicoId) {
        this.medicoId = medicoId;
    }

    /**
     * Obtiene la fecha y hora de la anotación[cite: 10].
     * @return La fecha y hora del registro[cite: 10].
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha y hora de la anotación[cite: 10].
     * @param fecha La fecha y hora a asignar[cite: 10].
     */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /**
     * Obtiene la descripción u observaciones de la anotación[cite: 10].
     * @return La descripción detallada[cite: 10].
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción u observaciones de la anotación[cite: 10].
     * @param descripcion La descripción a asignar[cite: 10].
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}