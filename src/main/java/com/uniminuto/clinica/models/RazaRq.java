package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Clase que representa la solicitud para crear una raza.
 */
@Data
public class RazaRq {

    /**
     * Identificador único de la raza.
     */
    private Integer razaId;

    /**
     * Nombre de la raza.
     */
    private String nombre;

    /**
     * Especie a la que pertenece la raza.
     */
    private String especie;

}
