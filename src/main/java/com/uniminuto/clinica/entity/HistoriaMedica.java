/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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
 * Entidad que representa la historia médica de un paciente (mascota).
 * Contiene el registro general de la historia clínica y se relaciona
 * con múltiples anotaciones de historia médica.
 *
 * @author Alma Hernandez
 */

@Entity
@Data
@Table(name = "historia_medica")

public class HistoriaMedica {
    /**
     * Identificador único y clave primaria de la historia médica.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador del paciente (mascota) al que pertenece esta historia médica.
     * Aunque el campo se llama {@code paciente_id}, en el modelo de la clínica
     * hace referencia a la tabla {@code mascota}.
     */
    @Column(name = "paciente_id", nullable = false)
    private Integer pacienteId;

    /**
     * Fecha y hora de creación del registro de la historia médica.
     * Por defecto, la base de datos asigna el timestamp actual.
     * Este campo se utiliza para filtrar historias por rango de fechas
     * y ordenarlas de la más reciente a la más antigua.
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}