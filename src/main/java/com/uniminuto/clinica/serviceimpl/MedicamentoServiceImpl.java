package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoServiceImpl(
            MedicamentoRepository medicamentoRepository
    ) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @Override
    public List<Medicamento> listarMedicamentos()
            throws BadRequestException {

        return medicamentoRepository.findAll();
    }

    @Override
    public Medicamento crearMedicamento(
            Medicamento medicamento
    ) throws BadRequestException {

        validarMedicamento(medicamento);

        medicamento.setNombre(
                medicamento.getNombre().trim()
        );

        medicamento.setDescripcion(
                medicamento.getDescripcion() != null
                        ? medicamento.getDescripcion().trim()
                        : null
        );

        medicamento.setPresentacion(
                medicamento.getPresentacion() != null
                        ? medicamento.getPresentacion().trim()
                        : null
        );

        medicamento.setFechaCreacionRegistro(
                LocalDateTime.now()
        );

        medicamento.setFechaModificacionRegistro(null);

        return medicamentoRepository.save(medicamento);
    }

    @Override
    public Medicamento actualizarMedicamento(
            Long id,
            Medicamento medicamento
    ) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException(
                    "El ID del medicamento es obligatorio"
            );
        }

        Medicamento existente =
                medicamentoRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "El medicamento no existe"
                                )
                        );

        validarMedicamento(medicamento);

        existente.setNombre(
                medicamento.getNombre().trim()
        );

        existente.setDescripcion(
                medicamento.getDescripcion() != null
                        ? medicamento.getDescripcion().trim()
                        : null
        );

        existente.setPresentacion(
                medicamento.getPresentacion() != null
                        ? medicamento.getPresentacion().trim()
                        : null
        );

        existente.setFechaCompra(
                medicamento.getFechaCompra()
        );

        existente.setFechaVence(
                medicamento.getFechaVence()
        );

        existente.setFechaModificacionRegistro(
                LocalDateTime.now()
        );

        return medicamentoRepository.save(existente);
    }

    private void validarMedicamento(
            Medicamento medicamento
    ) throws BadRequestException {

        if (medicamento == null) {
            throw new BadRequestException(
                    "El medicamento es obligatorio"
            );
        }

        if (medicamento.getNombre() == null
                || medicamento.getNombre().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre del medicamento es obligatorio"
            );
        }

        if (medicamento.getFechaCompra() != null
                && medicamento.getFechaVence() != null
                && medicamento.getFechaVence()
                        .isBefore(medicamento.getFechaCompra())) {

            throw new BadRequestException(
                    "La fecha de vencimiento no puede ser anterior a la fecha de compra"
            );
        }
    }
}