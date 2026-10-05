package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoriaMedicaServiceImpl
        implements HistoriaMedicaService {

    private final HistoriaMedicaRepository historiaMedicaRepository;

    public HistoriaMedicaServiceImpl(
            HistoriaMedicaRepository historiaMedicaRepository
    ) {
        this.historiaMedicaRepository =
                historiaMedicaRepository;
    }

    @Override
    public List<HistoriaMedica> listarHistorias()
            throws BadRequestException {

        return historiaMedicaRepository.findAll();
    }

    @Override
    public HistoriaMedica crearHistoria(
            HistoriaMedica historia
    ) throws BadRequestException {

        validarHistoria(historia);

        historia.setFechaCreacion(
                LocalDateTime.now()
        );

        return historiaMedicaRepository.save(historia);
    }

    @Override
    public HistoriaMedica actualizarHistoria(
            Long id,
            HistoriaMedica historia
    ) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException(
                    "El ID de la historia médica es obligatorio"
            );
        }

        HistoriaMedica existente =
                historiaMedicaRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "La historia médica no existe"
                                )
                        );

        validarHistoria(historia);

        existente.setPacienteId(
                historia.getPacienteId()
        );

        return historiaMedicaRepository.save(
                existente
        );
    }

    private void validarHistoria(
            HistoriaMedica historia
    ) throws BadRequestException {

        if (historia == null) {
            throw new BadRequestException(
                    "La historia médica es obligatoria"
            );
        }

        if (historia.getPacienteId() == null) {
            throw new BadRequestException(
                    "El paciente es obligatorio"
            );
        }
    }
}