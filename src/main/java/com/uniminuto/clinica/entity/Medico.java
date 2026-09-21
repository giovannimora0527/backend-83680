package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa a un médico veterinario de la clínica.
 */
@Entity
@Table(name = "medico")
@Data
public class Medico {

    /**
     * Identificador único del médico.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Tipo de documento de identidad (CC, CE, etc.).
     */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /**
     * Número del documento de identidad (único).
     */
    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    /**
     * Nombres del médico.
     */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del médico.
     */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /**
     * Teléfono de contacto.
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Número de registro profesional (único).
     */
    @Column(name = "registro_profesional", nullable = false, length = 50)
    private String registroProfesional;

    /**
     * Especialización del médico.
     */
    @ManyToOne
    @JoinColumn(name = "especializacion_id", nullable = false)
    private Especializacion especializacion;

}
