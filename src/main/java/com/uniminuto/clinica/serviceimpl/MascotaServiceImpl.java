package com.uniminuto.clinica.serviceimpl;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.MascotaService;

@Service
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteRepository clienteRepository;
    private final RazaRepository razaRepository;

    public MascotaServiceImpl(
            MascotaRepository mascotaRepository,
            ClienteRepository clienteRepository,
            RazaRepository razaRepository) {

        this.mascotaRepository = mascotaRepository;
        this.clienteRepository = clienteRepository;
        this.razaRepository = razaRepository;
    }

    @Override
    public List<Mascota> listarMascotas() throws BadRequestException {
        return mascotaRepository.findAll();
    }

    @Override
    public List<Mascota> listarMascotasPorCliente(Long clienteId)
            throws BadRequestException {

        if (clienteId == null) {
            throw new BadRequestException("El cliente es obligatorio");
        }

        if (!clienteRepository.existsById(clienteId)) {
            throw new BadRequestException("El cliente no existe");
        }

        return mascotaRepository.findByClienteClienteId(clienteId);
    }

    @Override
    public List<Mascota> listarMascotasPorRaza(Integer razaId)
            throws BadRequestException {

        if (razaId == null) {
            throw new BadRequestException("La raza es obligatoria");
        }

        if (!razaRepository.existsById(razaId)) {
            throw new BadRequestException("La raza no existe");
        }

        return mascotaRepository.findByRazaRazaId(razaId);
    }

    @Override
    public Mascota crearMascota(Mascota mascota)
            throws BadRequestException {

        if (mascota == null) {
            throw new BadRequestException("La mascota es obligatoria");
        }

        if (mascota.getNombreMascota() == null
                || mascota.getNombreMascota().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre de la mascota es obligatorio");
        }

        if (mascota.getEdad() == null || mascota.getEdad() < 0) {
            throw new BadRequestException(
                    "La edad de la mascota no es válida");
        }

        if (mascota.getCliente() == null
                || mascota.getCliente().getClienteId() == null) {

            throw new BadRequestException(
                    "El cliente es obligatorio");
        }

        if (mascota.getRaza() == null
                || mascota.getRaza().getRazaId() == null) {

            throw new BadRequestException(
                    "La raza es obligatoria");
        }

        Long clienteId = mascota.getCliente().getClienteId();
        Integer razaId = mascota.getRaza().getRazaId();

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "El cliente no existe"));

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "La raza no existe"));

        mascota.setCliente(cliente);
        mascota.setRaza(raza);

        mascota.setFechaRegistro(LocalDateTime.now());
        mascota.setFechaModificacion(null);

        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota actualizarMascota(
            Integer mascotaId,
            Mascota mascota)
            throws BadRequestException {

        if (mascotaId == null) {
            throw new BadRequestException(
                    "El ID de la mascota es obligatorio");
        }

        Mascota mascotaExistente = mascotaRepository.findById(mascotaId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "La mascota no existe"));

        if (mascota.getNombreMascota() == null
                || mascota.getNombreMascota().trim().isEmpty()) {

            throw new BadRequestException(
                    "El nombre de la mascota es obligatorio");
        }

        if (mascota.getEdad() == null || mascota.getEdad() < 0) {
            throw new BadRequestException(
                    "La edad de la mascota no es válida");
        }

        if (mascota.getCliente() == null
                || mascota.getCliente().getClienteId() == null) {

            throw new BadRequestException(
                    "El cliente es obligatorio");
        }

        if (mascota.getRaza() == null
                || mascota.getRaza().getRazaId() == null) {

            throw new BadRequestException(
                    "La raza es obligatoria");
        }

        Long clienteId = mascota.getCliente().getClienteId();
        Integer razaId = mascota.getRaza().getRazaId();

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "El cliente no existe"));

        Raza raza = razaRepository.findById(razaId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "La raza no existe"));

        mascotaExistente.setNombreMascota(
                mascota.getNombreMascota());

        mascotaExistente.setEdad(
                mascota.getEdad());

        mascotaExistente.setCliente(cliente);

        mascotaExistente.setRaza(raza);

        mascotaExistente.setFechaModificacion(
                LocalDateTime.now());

        return mascotaRepository.save(mascotaExistente);
    }
}