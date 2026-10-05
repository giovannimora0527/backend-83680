package com.uniminuto.clinica.serviceimpl;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.service.ClienteService;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // ==========================================
    // LISTAR CLIENTES
    // ==========================================
    @Override
    public List<Cliente> listarClientes() throws BadRequestException {

        return clienteRepository.findAll();
    }

    // ==========================================
    // CREAR CLIENTE
    // ==========================================
    @Override
    public Cliente crearCliente(Cliente cliente)
            throws BadRequestException {

        // Validar tipo de documento
        if (cliente.getTipoDocumento() == null ||
                cliente.getTipoDocumento().isBlank()) {

            throw new BadRequestException(
                    "El tipo de documento es obligatorio"
            );
        }

        // Validar número de documento
        if (cliente.getNumeroDocumento() == null ||
                cliente.getNumeroDocumento().isBlank()) {

            throw new BadRequestException(
                    "El número de documento es obligatorio"
            );
        }

        // Validar nombres
        if (cliente.getNombres() == null ||
                cliente.getNombres().isBlank()) {

            throw new BadRequestException(
                    "Los nombres son obligatorios"
            );
        }

        // Validar apellidos
        if (cliente.getApellidos() == null ||
                cliente.getApellidos().isBlank()) {

            throw new BadRequestException(
                    "Los apellidos son obligatorios"
            );
        }

        // Validar fecha de nacimiento
        if (cliente.getFechaNacimiento() == null) {

            throw new BadRequestException(
                    "La fecha de nacimiento es obligatoria"
            );
        }

        // ==========================================
        // VALIDAR CLIENTE DUPLICADO
        // ==========================================

        boolean existe = clienteRepository
                .existsByTipoDocumentoAndNumeroDocumento(
                        cliente.getTipoDocumento(),
                        cliente.getNumeroDocumento()
                );

        if (existe) {

            throw new BadRequestException(
                    "Ya existe un cliente con ese tipo y número de documento"
            );
        }

        // ==========================================
        // ESTADO POR DEFECTO
        // ==========================================

        if (cliente.getActivo() == null) {

            cliente.setActivo(true);
        }

        // Guardar cliente
        return clienteRepository.save(cliente);
    }

    // ==========================================
    // ACTUALIZAR CLIENTE
    // ==========================================
    @Override
    public Cliente actualizarCliente(
            Long clienteId,
            Cliente cliente
    ) throws BadRequestException {

        // ==========================================
        // BUSCAR CLIENTE
        // ==========================================

        Cliente clienteExistente = clienteRepository
                .findById(clienteId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "El cliente no existe"
                        )
                );

        // ==========================================
        // VALIDACIONES
        // ==========================================

        if (cliente.getTipoDocumento() == null ||
                cliente.getTipoDocumento().isBlank()) {

            throw new BadRequestException(
                    "El tipo de documento es obligatorio"
            );
        }

        if (cliente.getNumeroDocumento() == null ||
                cliente.getNumeroDocumento().isBlank()) {

            throw new BadRequestException(
                    "El número de documento es obligatorio"
            );
        }

        if (cliente.getNombres() == null ||
                cliente.getNombres().isBlank()) {

            throw new BadRequestException(
                    "Los nombres son obligatorios"
            );
        }

        if (cliente.getApellidos() == null ||
                cliente.getApellidos().isBlank()) {

            throw new BadRequestException(
                    "Los apellidos son obligatorios"
            );
        }

        if (cliente.getFechaNacimiento() == null) {

            throw new BadRequestException(
                    "La fecha de nacimiento es obligatoria"
            );
        }

        // ==========================================
        // VALIDAR DOCUMENTO DUPLICADO
        // ==========================================

        boolean existeOtroCliente = clienteRepository
                .existsByTipoDocumentoAndNumeroDocumentoAndClienteIdNot(
                        cliente.getTipoDocumento(),
                        cliente.getNumeroDocumento(),
                        clienteId
                );

        if (existeOtroCliente) {

            throw new BadRequestException(
                    "Ya existe otro cliente con ese tipo y número de documento"
            );
        }

        // ==========================================
        // ACTUALIZAR DATOS
        // ==========================================

        clienteExistente.setUsuarioId(
                cliente.getUsuarioId()
        );

        clienteExistente.setTipoDocumento(
                cliente.getTipoDocumento()
        );

        clienteExistente.setNumeroDocumento(
                cliente.getNumeroDocumento()
        );

        clienteExistente.setNombres(
                cliente.getNombres()
        );

        clienteExistente.setApellidos(
                cliente.getApellidos()
        );

        clienteExistente.setFechaNacimiento(
                cliente.getFechaNacimiento()
        );

        clienteExistente.setGenero(
                cliente.getGenero()
        );

        clienteExistente.setTelefono(
                cliente.getTelefono()
        );

        clienteExistente.setDireccion(
                cliente.getDireccion()
        );

        // Guardar cambios
        return clienteRepository.save(clienteExistente);
    }

    // ==========================================
    // CAMBIAR ESTADO
    // ==========================================
    @Override
    public Cliente cambiarEstado(
            Long clienteId,
            Boolean activo
    ) throws BadRequestException {

        Cliente cliente = clienteRepository
                .findById(clienteId)
                .orElseThrow(() ->
                        new BadRequestException(
                                "El cliente no existe"
                        )
                );

        cliente.setActivo(activo);

        return clienteRepository.save(cliente);
    }
}