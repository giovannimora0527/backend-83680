package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> listarMedicamentos()
            throws BadRequestException;

    Medicamento crearMedicamento(
            Medicamento medicamento
    ) throws BadRequestException;

    Medicamento actualizarMedicamento(
            Long id,
            Medicamento medicamento
    ) throws BadRequestException;
}