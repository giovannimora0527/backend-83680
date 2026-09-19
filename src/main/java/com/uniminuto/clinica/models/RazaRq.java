package com.uniminuto.clinica.models;

import lombok.Data;

@Data
/** Datos recibidos para registrar una raza. */
public class RazaRq {

    /** Identificador de la raza cuando corresponde. */
    private Integer razaId;
    /** Nombre de la raza. */
    private String nombre;
    /** Especie de la raza. */
    private String especie;

}
