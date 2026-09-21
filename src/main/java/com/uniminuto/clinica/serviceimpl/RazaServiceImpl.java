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

/**
 * Implementacion de la logica de las razas.
 */
@Service
public class RazaServiceImpl implements RazaService {

    /**
     * Repositorio de razas.
     */
    @Autowired
    private RazaRepository razaRepository;

    /**
     * Guarda una raza nueva. No permite repetir el mismo nombre en la misma especie.
     *
     * @param razaRq datos de la raza.
     * @return respuesta de exito.
     */
    @Override
    public MiRespuestaRS guardarRazaNueva(RazaRq razaRq) {

        validarObjetoEntrada(razaRq);

        if (this.razaRepository.existsByNombreAndEspecie(razaRq.getNombre(), razaRq.getEspecie())) {
            throw new BadRequestException("Ya existe la raza " + razaRq.getNombre()
                    + " de la especie " + razaRq.getEspecie());
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

    /**
     * Valida que la raza tenga nombre y especie.
     *
     * @param razaRq objeto a validar.
     */
    private void validarObjetoEntrada(RazaRq razaRq) {
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
