package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

/**
 * Entidad que representa a un cliente (dueño de una o varias mascotas).
 */
@Entity
@Table(name = "cliente")
@Data
public class Cliente {

    /**
     * Identificador único del cliente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Long clienteId;

    /**
     * Identificador del usuario de acceso asociado al cliente.
     */
    @Column(name = "usuario_id")
    private Integer usuarioId;

    /**
     * Tipo de documento de identidad (CC, CE, etc.).
     */
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    /**
     * Número del documento de identidad.
     */
    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    /**
     * Nombres del cliente.
     */
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del cliente.
     */
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    /**
     * Fecha de nacimiento del cliente.
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Género del cliente (un solo carácter).
     */
    @Column(name = "genero", length = 1)
    private String genero;

    /**
     * Teléfono de contacto.
     */
    @Column(name = "telefono", length = 20)
    private String telefono;

    /**
     * Dirección de residencia.
     */
    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    /**
     * Indica si el cliente está activo.
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo;

}
