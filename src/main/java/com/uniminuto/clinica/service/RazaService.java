package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import org.apache.coyote.BadRequestException;

/** Define las operaciones de negocio para razas. */
public interface RazaService {
    /** Valida y guarda una raza nueva. */

    MiRespuestaRS guardarRazaNueva(RazaRq razaRq) throws BadRequestException;
}
