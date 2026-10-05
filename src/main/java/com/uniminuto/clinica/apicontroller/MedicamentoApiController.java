package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.service.MedicamentoService;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.uniminuto.clinica.api.MedicamentoApi;

@RestController
public class MedicamentoApiController
        implements MedicamentoApi {

    private final MedicamentoService medicamentoService;

    public MedicamentoApiController(
            MedicamentoService medicamentoService
    ) {
        this.medicamentoService = medicamentoService;
    }

    @Override
    public ResponseEntity<List<Medicamento>> listarMedicamentos()
            throws BadRequestException {

        return ResponseEntity.ok(
                medicamentoService.listarMedicamentos()
        );
    }

    @Override
    public ResponseEntity<Medicamento> crearMedicamento(
            Medicamento medicamento
    ) throws BadRequestException {

        return ResponseEntity.ok(
                medicamentoService.crearMedicamento(
                        medicamento
                )
        );
    }

    @Override
    public ResponseEntity<Medicamento> actualizarMedicamento(
            Long id,
            Medicamento medicamento
    ) throws BadRequestException {

        return ResponseEntity.ok(
                medicamentoService.actualizarMedicamento(
                        id,
                        medicamento
                )
        );
    }
}