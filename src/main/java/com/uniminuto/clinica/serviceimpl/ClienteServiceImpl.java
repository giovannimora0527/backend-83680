package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
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
    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAllByOrderByNombresAscApellidosAsc();
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

    @Override
    public MiRespuestaRS guardarCliente(ClienteRq rq) {
        validar(rq);
        if (clienteRepository.existsByTipoDocumentoAndNumeroDocumento(
                rq.getTipoDocumento().trim(), rq.getNumeroDocumento().trim())) {
            throw new ConflictException("El propietario ya está registrado");
        }
        Cliente cliente = new Cliente();
        aplicar(cliente, rq);
        clienteRepository.save(cliente);
        return respuesta("Propietario registrado correctamente");
    }

    @Override
    public MiRespuestaRS actualizarCliente(ClienteRq rq) {
        validar(rq);
        if (rq.getClienteId() == null) {
            throw new BadRequestException("El ID del propietario es obligatorio");
        }
        Cliente cliente = clienteRepository.findById(rq.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        if (clienteRepository.existsByTipoDocumentoAndNumeroDocumentoAndClienteIdNot(
                rq.getTipoDocumento().trim(), rq.getNumeroDocumento().trim(), rq.getClienteId())) {
            throw new ConflictException("El documento ya está asociado a otro propietario");
        }
        aplicar(cliente, rq);
        clienteRepository.save(cliente);
        return respuesta("Propietario actualizado correctamente");
    }

    private void validar(ClienteRq rq) {
        if (rq == null || vacio(rq.getTipoDocumento()) || vacio(rq.getNumeroDocumento())
                || vacio(rq.getNombres()) || vacio(rq.getApellidos()) || rq.getFechaNacimiento() == null) {
            throw new BadRequestException("Tipo y número de documento, nombres, apellidos y fecha de nacimiento son obligatorios");
        }
        if (rq.getFechaNacimiento().isAfter(java.time.LocalDate.now())) {
            throw new BadRequestException("La fecha de nacimiento no puede ser futura");
        }
    }

    private void aplicar(Cliente cliente, ClienteRq rq) {
        cliente.setTipoDocumento(rq.getTipoDocumento().trim().toUpperCase());
        cliente.setNumeroDocumento(rq.getNumeroDocumento().trim());
        cliente.setNombres(rq.getNombres().trim());
        cliente.setApellidos(rq.getApellidos().trim());
        cliente.setFechaNacimiento(rq.getFechaNacimiento());
        cliente.setGenero(rq.getGenero());
        cliente.setTelefono(rq.getTelefono());
        cliente.setDireccion(rq.getDireccion());
        cliente.setActivo(rq.getActivo() == null || rq.getActivo());
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private MiRespuestaRS respuesta(String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
