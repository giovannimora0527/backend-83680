package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.RazaService;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RazaServiceImpl implements RazaService {

    private final RazaRepository razaRepository;

    public RazaServiceImpl(RazaRepository razaRepository) {
        this.razaRepository = razaRepository;
    }

    @Override
    public List<Raza> listarRazas() throws BadRequestException {
        return razaRepository.findAll();
    }

    @Override
    public Raza crearRaza(Raza raza) throws BadRequestException {

        if (raza == null) {
            throw new BadRequestException(
                    "La raza es obligatoria"
            );
        }

        if (raza.getNombre() == null ||
                raza.getNombre().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre de la raza es obligatorio"
            );
        }

        if (raza.getEspecie() == null ||
                raza.getEspecie().trim().isEmpty()) {

            throw new BadRequestException(
                    "La especie es obligatoria"
            );
        }

        raza.setNombre(raza.getNombre().trim());
        raza.setEspecie(raza.getEspecie().trim());

        raza.setFechaCreacion(LocalDateTime.now());
        raza.setFechaModificacion(null);

        return razaRepository.save(raza);
    }

    @Override
    public Raza actualizarRaza(
            Integer razaId,
            Raza raza)
            throws BadRequestException {

        if (razaId == null) {
            throw new BadRequestException(
                    "El ID de la raza es obligatorio"
            );
        }

        Raza razaExistente = razaRepository.findById(razaId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "La raza no existe"
                        )
                );

        if (raza.getNombre() == null ||
                raza.getNombre().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre de la raza es obligatorio"
            );
        }

        if (raza.getEspecie() == null ||
                raza.getEspecie().trim().isEmpty()) {

            throw new BadRequestException(
                    "La especie es obligatoria"
            );
        }

        razaExistente.setNombre(
                raza.getNombre().trim()
        );

        razaExistente.setEspecie(
                raza.getEspecie().trim()
        );

        razaExistente.setFechaModificacion(
                LocalDateTime.now()
        );

        return razaRepository.save(razaExistente);
    }
}