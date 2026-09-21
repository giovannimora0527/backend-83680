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
 * Entidad que representa una fórmula médica en el sistema de la clínica veterinaria.
 * Contiene información sobre medicamentos recetados, dosis e indicaciones asociadas
 * a una cita veterinaria específica.
 * <p>
 * Tabla de base de datos: {@code formula_medica}
 * </p>
 *
 * @author Tu Nombre (¡Cámbialo!)
 * @version 1.0
 */
@Entity
@Table(name = "formula_medica")
@Data
public class FormulaMedica {

    /**
     * Identificador único y clave primaria de la fórmula médica.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador de la cita veterinaria asociada a esta fórmula médica.
     * Relación con la tabla {@code cita}.
     */
    @Column(name = "cita_id", nullable = false)
    private Integer citaId;

    /**
     * Identificador del medicamento recetado.
     * Relación con la tabla {@code medicamento}.
     */
    @Column(name = "medicamento_id", nullable = false)
    private Integer medicamentoId;

    /**
     * Dosis del medicamento recetado al paciente.
     * Campo obligatorio que describe la cantidad y frecuencia de administración.
     */
    @Column(name = "dosis", nullable = false, columnDefinition = "TEXT")
    private String dosis;

    /**
     * Indicaciones adicionales sobre el uso del medicamento.
     * Puede incluir recomendaciones de horario, alimentación, etc.
     */
    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    /**
     * Fecha y hora de creación del registro de la fórmula médica.
     * Este campo es utilizado para ordenar las fórmulas de la más reciente
     * a la más antigua.
     */
    @Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /**
     * Fecha y hora de la última actualización del registro.
     * Puede ser nulo si el registro no ha sido modificado.
     */
    @Column(name = "fecha_actualizacion_registro")
    private LocalDateTime fechaActualizacionRegistro;
}