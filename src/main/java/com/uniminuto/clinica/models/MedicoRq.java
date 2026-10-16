package com.uniminuto.clinica.models;

import lombok.Data;

/**
 * Datos que llegan del front para crear o editar un medico.
 */
@Data
public class MedicoRq {
    /** Id del medico (solo para editar). */
    private Long medicoId;
    /** Tipo de documento: CC, CE o PA. */
    private String tipoDocumento;
    /** Numero de documento. */
    private String numeroDocumento;
    /** Nombres. */
    private String nombres;
    /** Apellidos. */
    private String apellidos;
    /** Telefono. */
    private String telefono;
    /** Registro profesional (tarjeta profesional). */
    private String registroProfesional;
    /** Id de la especializacion. */
    private Long especializacionId;
}
