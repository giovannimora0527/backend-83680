package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

/**
 * Datos que llegan del front para crear o editar un cliente.
 */
@Data
public class ClienteRq {
    /** Id del cliente (solo para editar). */
    private Long clienteId;
    /** Tipo de documento: CC, TI, CE o PA. */
    private String tipoDocumento;
    /** Numero de documento. */
    private String numeroDocumento;
    /** Nombres. */
    private String nombres;
    /** Apellidos. */
    private String apellidos;
    /** Fecha de nacimiento. */
    private LocalDate fechaNacimiento;
    /** Genero: M, F u O. */
    private String genero;
    /** Telefono. */
    private String telefono;
    /** Direccion. */
    private String direccion;
    /** Si el cliente esta activo (solo al editar). */
    private Boolean activo;
}
