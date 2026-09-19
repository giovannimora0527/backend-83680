package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.MascotaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RazaRepository razaRepository;

    @Override
    public List<Mascota> listarMascotas() throws BadRequestException {
        return mascotaRepository.findAllByOrderByNombreMascotaAsc();
    }

    @Override
    public List<Mascota> listarMascotasPorCliente(Long clienteId) throws BadRequestException {
        if (clienteId == null) {
            throw new BadRequestException("El ID del cliente no puede ser nulo");
        }

        Optional<Cliente> optCliente = clienteRepository
                .findById(clienteId);

        if (optCliente.isEmpty()) {
            throw new BadRequestException("Cliente no encontrado");
        }

        return mascotaRepository.findAllByClienteOrderByNombreMascotaAsc(optCliente.get());
    }

    @Override
    public List<Mascota> listarMascotasPorRaza(Integer razaId) throws BadRequestException {
        if (razaId == null) {
            throw new BadRequestException("El ID de la raza no puede ser nulo");
        }

        Optional<Raza> optRaza = razaRepository.findById(razaId);

        if (optRaza.isEmpty()) {
            throw new BadRequestException("Raza no encontrada");
        }

        return mascotaRepository.findAllByRazaOrderByNombreMascotaAsc(optRaza.get());
    }

    @Override
    public MiRespuestaRS guardarMascota(MascotaRq mascotaRq) throws BadRequestException {
        // Paso 1. Creo un validador del objeto de entrada
        this.validarObjetoMascota(mascotaRq);

        // paso 2. Busco el cliente y la raza en la base de datos
        Optional<Cliente> optCliente = clienteRepository
                .findById(mascotaRq.getClienteId());
        if (optCliente.isEmpty()) {
            throw new BadRequestException("Cliente no encontrado");
        }

        Optional<Raza> optRaza = razaRepository
                .findById(mascotaRq.getRazaId());
        if (optRaza.isEmpty()) {
            throw new BadRequestException("Raza no encontrada");
        }

        // Paso 3. Crear el objeto mascota para guardar.
        Mascota mascota = new Mascota();
        mascota.setNombreMascota(mascotaRq.getNombreMascota());
        mascota.setCliente(optCliente.get());
        mascota.setRaza(optRaza.get());
        mascota.setEdad(mascotaRq.getEdad());
        mascota.setFechaRegistro(LocalDateTime.now());

        // Paso 4. Guardar el objeto.
        this.mascotaRepository.save(mascota);

        // Paso 5. Devolver la respuesta de éxito.
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Mascota guardada correctamente");

        return rta;
    }

    @Override
    public MiRespuestaRS actualizarMascota(MascotaRq mascotaRq) throws BadRequestException {
        // Paso 1. Creo un validador del objeto de entrada
        this.validarObjetoMascota(mascotaRq);

        // paso 2. Busco el cliente y la raza en la base de datos
        Optional<Cliente> optCliente = clienteRepository
                .findById(mascotaRq.getClienteId());
        if (optCliente.isEmpty()) {
            throw new BadRequestException("Cliente no encontrado");
        }

        Optional<Raza> optRaza = razaRepository
                .findById(mascotaRq.getRazaId());
        if (optRaza.isEmpty()) {
            throw new BadRequestException("Raza no encontrada");
        }

        // Paso 3. Busco la mascota en la base de datos
        Optional<Mascota> optMascota = mascotaRepository
                .findById(mascotaRq.getMascotaId());
        if (optMascota.isEmpty()) {
            throw new BadRequestException("Mascota no encontrada");
        }

        Mascota mascotaActualizar = optMascota.get();
        // Paso 4. Actualizar los datos de la mascota
        mascotaActualizar.setNombreMascota(mascotaRq.getNombreMascota());
        mascotaActualizar.setCliente(optCliente.get());
        mascotaActualizar.setRaza(optRaza.get());
        mascotaActualizar.setEdad(mascotaRq.getEdad());
        mascotaActualizar.setFechaModificacion(LocalDateTime.now());

        // Paso 5. Guardar el objeto actualizado.
        this.mascotaRepository.save(mascotaActualizar);

        // Paso 6. Devolver la respuesta de éxito.
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Mascota actualizada correctamente");

        return rta;
    }

    private void validarObjetoMascota(MascotaRq mascotaRq) throws BadRequestException {
       if (mascotaRq == null) {
           throw new BadRequestException("El objeto MascotaRq no puede ser nulo");
       }

       if (mascotaRq.getNombreMascota() == null || mascotaRq.getNombreMascota().isEmpty()) {
           throw new BadRequestException("El nombre de la mascota no puede ser nulo o vacío");
       }

       if (mascotaRq.getEdad() == null || mascotaRq.getEdad() < 0) {
           throw new BadRequestException("La edad de la mascota no puede ser nula o negativa");
       }

       if (mascotaRq.getClienteId() == null || mascotaRq.getClienteId() < 0) {
           throw new BadRequestException("El ID del cliente no puede ser nulo o negativo");
       }

       if (mascotaRq.getRazaId() == null || mascotaRq.getRazaId() < 0) {
           throw new BadRequestException("El ID de la raza no puede ser nulo o negativo");
       }

    }



}
