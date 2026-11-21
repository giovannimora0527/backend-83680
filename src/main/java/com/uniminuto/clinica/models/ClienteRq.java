package com.uniminuto.clinica.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteRq {
    private Long clienteId;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
    private String direccion;
    private Boolean activo;
}
