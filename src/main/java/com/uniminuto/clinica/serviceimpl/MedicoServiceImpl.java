package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecializacionRepository especializacionRepository;

    public MedicoServiceImpl(
            MedicoRepository medicoRepository,
            EspecializacionRepository especializacionRepository
    ) {
        this.medicoRepository = medicoRepository;
        this.especializacionRepository = especializacionRepository;
    }

    @Override
    public List<Medico> listarMedicos()
            throws BadRequestException {

        return medicoRepository.findAll();
    }

    @Override
    public Medico crearMedico(
            Medico medico
    ) throws BadRequestException {

        validarMedico(medico);

        Especializacion especializacion =
                obtenerEspecializacion(
                        medico.getEspecializacion()
                );

        medico.setEspecializacion(especializacion);

        medico.setTipoDocumento(
                medico.getTipoDocumento().trim()
        );

        medico.setNumeroDocumento(
                medico.getNumeroDocumento().trim()
        );

        medico.setNombres(
                medico.getNombres().trim()
        );

        medico.setApellidos(
                medico.getApellidos().trim()
        );

        medico.setRegistroProfesional(
                medico.getRegistroProfesional().trim()
        );

        if (medico.getTelefono() != null) {
            medico.setTelefono(
                    medico.getTelefono().trim()
            );
        }

        return medicoRepository.save(medico);
    }

    @Override
    public Medico actualizarMedico(
            Long id,
            Medico medico
    ) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException(
                    "El ID del médico es obligatorio"
            );
        }

        Medico medicoExistente =
                medicoRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "El médico no existe"
                                )
                        );

        validarMedico(medico);

        Especializacion especializacion =
                obtenerEspecializacion(
                        medico.getEspecializacion()
                );

        medicoExistente.setTipoDocumento(
                medico.getTipoDocumento().trim()
        );

        medicoExistente.setNumeroDocumento(
                medico.getNumeroDocumento().trim()
        );

        medicoExistente.setNombres(
                medico.getNombres().trim()
        );

        medicoExistente.setApellidos(
                medico.getApellidos().trim()
        );

        medicoExistente.setTelefono(
                medico.getTelefono() != null
                        ? medico.getTelefono().trim()
                        : null
        );

        medicoExistente.setRegistroProfesional(
                medico.getRegistroProfesional().trim()
        );

        medicoExistente.setEspecializacion(
                especializacion
        );

        return medicoRepository.save(medicoExistente);
    }

    private void validarMedico(
            Medico medico
    ) throws BadRequestException {

        if (medico == null) {
            throw new BadRequestException(
                    "El médico es obligatorio"
            );
        }

        if (medico.getTipoDocumento() == null
                || medico.getTipoDocumento().trim().isEmpty()) {

            throw new BadRequestException(
                    "El tipo de documento es obligatorio"
            );
        }

        if (medico.getNumeroDocumento() == null
                || medico.getNumeroDocumento().trim().isEmpty()) {

            throw new BadRequestException(
                    "El número de documento es obligatorio"
            );
        }

        if (medico.getNombres() == null
                || medico.getNombres().trim().isEmpty()) {

            throw new BadRequestException(
                    "Los nombres son obligatorios"
            );
        }

        if (medico.getApellidos() == null
                || medico.getApellidos().trim().isEmpty()) {

            throw new BadRequestException(
                    "Los apellidos son obligatorios"
            );
        }

        if (medico.getRegistroProfesional() == null
                || medico.getRegistroProfesional().trim().isEmpty()) {

            throw new BadRequestException(
                    "El registro profesional es obligatorio"
            );
        }

        if (medico.getEspecializacion() == null
                || medico.getEspecializacion().getId() == null) {

            throw new BadRequestException(
                    "La especialización es obligatoria"
            );
        }
    }

    private Especializacion obtenerEspecializacion(
            Especializacion especializacion
    ) throws BadRequestException {

        if (especializacion == null
                || especializacion.getId() == null) {

            throw new BadRequestException(
                    "La especialización es obligatoria"
            );
        }

        return especializacionRepository
                .findById(especializacion.getId())
                .orElseThrow(() ->
                        new BadRequestException(
                                "La especialización no existe"
                        )
                );
    }
}