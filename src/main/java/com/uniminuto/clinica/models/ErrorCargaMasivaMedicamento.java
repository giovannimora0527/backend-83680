package com.uniminuto.clinica.models;

import lombok.Data;

@Data
/** Detalle de una fila rechazada durante la carga masiva. */
public class ErrorCargaMasivaMedicamento {

    /** Número de fila del archivo. */
    private int fila;

    /** Mensaje que explica el motivo del rechazo. */
    private String mensaje;
}
