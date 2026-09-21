package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> obtenerMedicamentos() throws BadRequestException;
}
