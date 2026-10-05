package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.service.EspecializacionService;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecializacionServiceImpl implements EspecializacionService {

    private final EspecializacionRepository especializacionRepository;

    public EspecializacionServiceImpl(
            EspecializacionRepository especializacionRepository
    ) {
        this.especializacionRepository = especializacionRepository;
    }

    @Override
    public List<Especializacion> listarEspecializaciones()
            throws BadRequestException {

        return especializacionRepository.findAll();
    }

    @Override
    public Especializacion crearEspecializacion(
            Especializacion especializacion
    ) throws BadRequestException {

        if (especializacion == null) {
            throw new BadRequestException(
                    "La especialización es obligatoria"
            );
        }

        if (especializacion.getNombre() == null
                || especializacion.getNombre().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre de la especialización es obligatorio"
            );
        }

        if (especializacion.getCodigoEspecializacion() == null
                || especializacion.getCodigoEspecializacion()
                        .trim().isEmpty()) {

            throw new BadRequestException(
                    "El código de especialización es obligatorio"
            );
        }

        String nombre = especializacion.getNombre().trim();
        String codigo = especializacion
                .getCodigoEspecializacion()
                .trim();

        if (especializacionRepository.existsByNombre(nombre)) {
            throw new BadRequestException(
                    "Ya existe una especialización con ese nombre"
            );
        }

        if (especializacionRepository
                .existsByCodigoEspecializacion(codigo)) {

            throw new BadRequestException(
                    "Ya existe una especialización con ese código"
            );
        }

        especializacion.setNombre(nombre);
        especializacion.setCodigoEspecializacion(codigo);

        if (especializacion.getDescripcion() != null) {
            especializacion.setDescripcion(
                    especializacion.getDescripcion().trim()
            );
        }

        return especializacionRepository.save(especializacion);
    }

    @Override
    public Especializacion actualizarEspecializacion(
            Long id,
            Especializacion especializacion
    ) throws BadRequestException {

        if (id == null) {
            throw new BadRequestException(
                    "El ID de la especialización es obligatorio"
            );
        }

        Especializacion existente = especializacionRepository
                .findById(id)
                .orElseThrow(() ->
                        new BadRequestException(
                                "La especialización no existe"
                        )
                );

        if (especializacion == null) {
            throw new BadRequestException(
                    "La especialización es obligatoria"
            );
        }

        if (especializacion.getNombre() == null
                || especializacion.getNombre().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre de la especialización es obligatorio"
            );
        }

        if (especializacion.getCodigoEspecializacion() == null
                || especializacion.getCodigoEspecializacion()
                        .trim().isEmpty()) {

            throw new BadRequestException(
                    "El código de especialización es obligatorio"
            );
        }

        String nombre = especializacion.getNombre().trim();
        String codigo = especializacion
                .getCodigoEspecializacion()
                .trim();

        if (especializacionRepository
                .existsByNombreAndIdNot(nombre, id)) {

            throw new BadRequestException(
                    "Ya existe otra especialización con ese nombre"
            );
        }

        if (especializacionRepository
                .existsByCodigoEspecializacionAndIdNot(codigo, id)) {

            throw new BadRequestException(
                    "Ya existe otra especialización con ese código"
            );
        }

        existente.setNombre(nombre);
        existente.setCodigoEspecializacion(codigo);

        if (especializacion.getDescripcion() != null) {
            existente.setDescripcion(
                    especializacion.getDescripcion().trim()
            );
        } else {
            existente.setDescripcion(null);
        }

        return especializacionRepository.save(existente);
    }
}