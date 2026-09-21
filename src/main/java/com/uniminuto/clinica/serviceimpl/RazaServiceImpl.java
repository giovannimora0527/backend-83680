package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.RazaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
/** Implementa las operaciones de razas. */
public class RazaServiceImpl implements RazaService {

    @Autowired
    /** Repositorio de razas. */
    private RazaRepository razaRepository;

    @Override
    // Valida los datos, evita duplicados y guarda una nueva raza.
    public MiRespuestaRS guardarRazaNueva(RazaRq razaRq) throws BadRequestException {

        validarObjetoEntrada(razaRq);

        Optional<Raza> optRaza = this.razaRepository
                .findByNombre(razaRq.getNombre());
        if (optRaza.isPresent()){
            throw new BadRequestException("Ya existe una raza con el nombre: " + razaRq.getNombre());
        }

        optRaza = this.razaRepository
                .findByEspecie(razaRq.getEspecie());
        if (optRaza.isPresent()){
            throw new BadRequestException("Ya existe una raza con la especie: " + razaRq.getEspecie());
        }

        Raza razaNueva = new Raza();
        razaNueva.setNombre(razaRq.getNombre());
        razaNueva.setEspecie(razaRq.getEspecie());
        razaNueva.setFechaCreacion(LocalDateTime.now());
        this.razaRepository.save(razaNueva);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Raza creada exitosamente");

        return rta;
    }

    private void validarObjetoEntrada(RazaRq razaRq) throws BadRequestException {
        // Comprueba que la solicitud tenga nombre y especie.
        if (razaRq == null) {
            throw new BadRequestException("El objeto RazaRq no puede ser nulo");
        }

        if (razaRq.getNombre() == null || razaRq.getNombre().isEmpty()) {
            throw new BadRequestException("El nombre de la raza no puede ser nulo o vacío");
        }

        if (razaRq.getEspecie() == null || razaRq.getEspecie().isEmpty()) {
            throw new BadRequestException("La especie de la raza no puede ser nulo o vacío");
        }
    }
}
