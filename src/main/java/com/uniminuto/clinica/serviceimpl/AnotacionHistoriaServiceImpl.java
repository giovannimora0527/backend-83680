package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnotacionHistoriaServiceImpl
        implements AnotacionHistoriaService {

    private final AnotacionHistoriaRepository
            anotacionHistoriaRepository;

    public AnotacionHistoriaServiceImpl(
            AnotacionHistoriaRepository anotacionHistoriaRepository
    ) {
        this.anotacionHistoriaRepository =
                anotacionHistoriaRepository;
    }

    @Override
    public List<AnotacionHistoria> listarAnotaciones()
            throws BadRequestException {

        return anotacionHistoriaRepository.findAll();
    }

    @Override
    public List<AnotacionHistoria> listarPorRango(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal
    ) throws BadRequestException {

        if (fechaInicial == null) {
            throw new BadRequestException(
                    "La fecha inicial es obligatoria"
            );
        }

        if (fechaFinal == null) {
            throw new BadRequestException(
                    "La fecha final es obligatoria"
            );
        }

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException(
                    "La fecha inicial no puede ser posterior a la fecha final"
            );
        }

        return anotacionHistoriaRepository
                .findByFechaBetweenOrderByFechaDesc(
                        fechaInicial,
                        fechaFinal
                );
    }

    @Override
    public AnotacionHistoria crearAnotacion(
            AnotacionHistoria anotacion
    ) throws BadRequestException {

        validarAnotacion(anotacion);

        anotacion.setFecha(
                LocalDateTime.now()
        );

        anotacion.setDescripcion(
                anotacion.getDescripcion().trim()
        );

        return anotacionHistoriaRepository.save(
                anotacion
        );
    }

    @Override
    public AnotacionHistoria actualizarAnotacion(
            Long id,
            AnotacionHistoria anotacion
    ) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException(
                    "El ID de la anotación es obligatorio"
            );
        }

        AnotacionHistoria existente =
                anotacionHistoriaRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "La anotación no existe"
                                )
                        );

        validarAnotacion(anotacion);

        existente.setHistoriaId(
                anotacion.getHistoriaId()
        );

        existente.setMedicoId(
                anotacion.getMedicoId()
        );

        existente.setDescripcion(
                anotacion.getDescripcion().trim()
        );

        return anotacionHistoriaRepository.save(
                existente
        );
    }

    private void validarAnotacion(
            AnotacionHistoria anotacion
    ) throws BadRequestException {

        if (anotacion == null) {
            throw new BadRequestException(
                    "La anotación es obligatoria"
            );
        }

        if (anotacion.getHistoriaId() == null) {
            throw new BadRequestException(
                    "La historia médica es obligatoria"
            );
        }

        if (anotacion.getMedicoId() == null) {
            throw new BadRequestException(
                    "El médico es obligatorio"
            );
        }

        if (anotacion.getDescripcion() == null
                || anotacion.getDescripcion()
                        .trim()
                        .isEmpty()) {

            throw new BadRequestException(
                    "La descripción de la anotación es obligatoria"
            );
        }
    }
}