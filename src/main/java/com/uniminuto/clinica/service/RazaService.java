package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import org.apache.coyote.BadRequestException;

public interface RazaService {

    MiRespuestaRS guardarRazaNueva(RazaRq razaRq) throws BadRequestException;
}
