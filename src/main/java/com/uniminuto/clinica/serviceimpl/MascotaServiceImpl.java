package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.RazaRepository;
import com.uniminuto.clinica.service.MascotaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
