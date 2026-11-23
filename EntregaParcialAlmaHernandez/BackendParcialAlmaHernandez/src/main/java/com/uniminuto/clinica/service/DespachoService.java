package com.uniminuto.clinica.service;

import com.uniminuto.clinica.model.DespachoConfirmarRq;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;

public interface DespachoService {
    List<Map<String, Object>> listarPendientes();
    RespuestaRs confirmar(String username, DespachoConfirmarRq request) throws BadRequestException;
}
