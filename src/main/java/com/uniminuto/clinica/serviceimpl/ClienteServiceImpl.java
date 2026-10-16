package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/** Implementa las operaciones de clientes. */
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    /** Repositorio de clientes. */
    private ClienteRepository clienteRepository;

    @Override
    // Obtiene los clientes activos ordenados por nombre para usarlos en los formularios.
    public List<Cliente> listarClientes() throws BadRequestException {
        return clienteRepository.findByActivoTrueOrderByNombresAscApellidosAsc();
    }

    @Override
    // Valida el identificador y devuelve el cliente si existe.
    public Cliente consultarCliente(Long clienteId) throws BadRequestException {
        if (clienteId == null) {
            throw new BadRequestException("El ID del cliente no puede ser nulo");
        }
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }
}
