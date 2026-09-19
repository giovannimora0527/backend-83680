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

@Entity
@Table(name = "Medico")
@Data
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    /** Identificador del médico. */
    private Long id;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    /** Tipo de documento del médico. */
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    /** Número de documento del médico. */
    private String numeroDocumento;

    @Column(name = "nombres", nullable = false, length = 100)
    /** Nombres del médico. */
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    /** Apellidos del médico. */
    private String apellidos;

    @Column(name = "telefono", length = 20)
    /** Teléfono de contacto. */
    private String telefono;

    @Column(name = "registro_profesional", nullable = false, length = 50)
    /** Registro profesional del médico. */
    private String registroProfesional;

    @ManyToOne
    @JoinColumn(name = "especializacion_id", nullable = false)
    /** Especialización del médico. */
    private Especializacion especializacion;


}
