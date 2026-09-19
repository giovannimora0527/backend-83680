package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Data
/** Representa al cliente o propietario de una mascota. */
public class Cliente {

    /** Identificador del cliente. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "usuario_id")
    /** Identificador del usuario relacionado. */
    private Integer usuarioId;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    /** Tipo de documento del cliente. */
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    /** Número de documento del cliente. */
    private String numeroDocumento;

    @Column(name = "nombres", nullable = false, length = 100)
    /** Nombres del cliente. */
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    /** Apellidos del cliente. */
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    /** Fecha de nacimiento del cliente. */
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 1)
    /** Género registrado para el cliente. */
    private String genero;

    @Column(name = "telefono", length = 20)
    /** Teléfono de contacto. */
    private String telefono;

    @Column(name = "direccion", columnDefinition = "TEXT")
    /** Dirección de residencia. */
    private String direccion;

    @Column(name = "activo", nullable = false)
    /** Indica si el cliente está activo. */
    private Boolean activo;

}
    /** Identificador del usuario relacionado. */
    /** Tipo de documento del cliente. */
    /** Número de documento del cliente. */
    /** Nombres del cliente. */
    /** Apellidos del cliente. */
    /** Fecha de nacimiento del cliente. */
    /** Género registrado para el cliente. */
    /** Teléfono de contacto. */
    /** Dirección de residencia. */
    /** Indica si el cliente está activo. */
