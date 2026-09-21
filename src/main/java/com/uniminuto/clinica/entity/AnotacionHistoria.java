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
 * Entidad que representa una anotación dentro de una historia médica.
 * Cada anotación registra una observación o evento médico realizado
 * por un médico veterinario en una fecha específica.
 *
 * Esta entidad tiene una relación con {@link HistoriaMedica} a través
 * del campo {@code historia_id}.
 
 * Tabla de base de datos: {@code anotacion_historia}
 * 
 * @author Alma Hernandez
 */

@Entity
@Data
@Table(name = "anotacion_historia")

public class AnotacionHistoria {
    /**
     * Identificador único y clave primaria de la anotación.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador de la historia médica a la que pertenece esta anotación.
     * Relación con la tabla {@code historia_medica}.
     */
    @Column(name = "historia_id", nullable = false)
    private Integer historiaId;

    /**
     * Identificador del médico veterinario que realizó la anotación.
     * Relación con la tabla {@code medico}.
     */
    @Column(name = "medico_id", nullable = false)
    private Integer medicoId;

    /**
     * Fecha y hora en la que se realizó la anotación.
     * Por defecto, la base de datos asigna el timestamp actual.
     * Este campo se utiliza para filtrar anotaciones por rango de fechas.
     */
    @Column(name = "fecha")
    private LocalDateTime fecha;

    /**
     * Descripción detallada de la anotación médica.
     * Contiene la observación, diagnóstico, tratamiento u otro
     * registro relevante realizado por el médico veterinario.
     */
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;
}