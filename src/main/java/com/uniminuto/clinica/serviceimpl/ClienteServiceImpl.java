package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de los clientes.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    /** Tipos de documento permitidos. */
    private static final List<String> TIPOS_DOCUMENTO = List.of("CC", "TI", "CE", "PA");

    /** Generos permitidos. */
    private static final List<String> GENEROS = List.of("M", "F", "O");

    /** Repositorio de clientes. */
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAllByOrderByNombresAscApellidosAsc();
    }

    @Override
    public Cliente consultarCliente(Long clienteId) {
        if (clienteId == null || clienteId <= 0) {
            throw new BadRequestException("El ID del cliente no puede ser nulo o menor que 1");
        }
        Optional<Cliente> optCliente = clienteRepository.findById(clienteId);
        if (optCliente.isEmpty()) {
            throw new BadRequestException("Cliente no encontrado");
        }
        return optCliente.get();
    }

    @Override
    public MiRespuestaRS guardarCliente(ClienteRq clienteRq) {
        // Paso 1. Validar los datos
        this.validarObjetoCliente(clienteRq);

        // Paso 2. Evitar documentos repetidos
        if (clienteRepository.existsByNumeroDocumento(clienteRq.getNumeroDocumento().trim())) {
            throw new BadRequestException("Ya existe un cliente con ese número de documento");
        }

        // Paso 3. Crear y guardar el cliente (queda activo)
        Cliente cliente = new Cliente();
        this.copiarDatos(clienteRq, cliente);
        cliente.setActivo(true);
        clienteRepository.save(cliente);
        return this.respuestaExito("Cliente guardado correctamente");
    }

    @Override
    public MiRespuestaRS actualizarCliente(ClienteRq clienteRq) {
        // Paso 1. Validar los datos y buscar el cliente
        this.validarObjetoCliente(clienteRq);
        Cliente cliente = this.consultarCliente(clienteRq.getClienteId());

        // Paso 2. El documento no puede quedar igual al de otro cliente
        if (clienteRepository.existsByNumeroDocumentoAndClienteIdNot(
                clienteRq.getNumeroDocumento().trim(), cliente.getClienteId())) {
            throw new BadRequestException("Ya existe otro cliente con ese número de documento");
        }

        // Paso 3. Actualizar y guardar
        this.copiarDatos(clienteRq, cliente);
        if (clienteRq.getActivo() != null) {
            cliente.setActivo(clienteRq.getActivo());
        }
        clienteRepository.save(cliente);
        return this.respuestaExito("Cliente actualizado correctamente");
    }

    /**
     * Pasa los datos del request a la entidad.
     *
     * @param clienteRq datos que llegan.
     * @param cliente   entidad a llenar.
     */
    private void copiarDatos(ClienteRq clienteRq, Cliente cliente) {
        cliente.setTipoDocumento(clienteRq.getTipoDocumento().toUpperCase());
        cliente.setNumeroDocumento(clienteRq.getNumeroDocumento().trim());
        cliente.setNombres(clienteRq.getNombres().trim());
        cliente.setApellidos(clienteRq.getApellidos().trim());
        cliente.setFechaNacimiento(clienteRq.getFechaNacimiento());
        cliente.setGenero(clienteRq.getGenero());
        cliente.setTelefono(clienteRq.getTelefono());
        cliente.setDireccion(clienteRq.getDireccion());
    }

    /**
     * Valida los campos obligatorios y su formato.
     *
     * @param clienteRq datos del cliente.
     */
    private void validarObjetoCliente(ClienteRq clienteRq) {
        if (clienteRq == null) {
            throw new BadRequestException("Los datos del cliente son obligatorios");
        }
        if (clienteRq.getTipoDocumento() == null
                || !TIPOS_DOCUMENTO.contains(clienteRq.getTipoDocumento().toUpperCase())) {
            throw new BadRequestException("El tipo de documento debe ser CC, TI, CE o PA");
        }
        if (clienteRq.getNumeroDocumento() == null || !clienteRq.getNumeroDocumento().trim().matches("\\d{5,20}")) {
            throw new BadRequestException("El número de documento debe tener entre 5 y 20 dígitos");
        }
        if (clienteRq.getNombres() == null || clienteRq.getNombres().isBlank()) {
            throw new BadRequestException("Los nombres son obligatorios");
        }
        if (clienteRq.getApellidos() == null || clienteRq.getApellidos().isBlank()) {
            throw new BadRequestException("Los apellidos son obligatorios");
        }
        if (clienteRq.getFechaNacimiento() == null) {
            throw new BadRequestException("La fecha de nacimiento es obligatoria");
        }
        if (clienteRq.getFechaNacimiento().isAfter(LocalDate.now().minusYears(18))) {
            throw new BadRequestException("El cliente debe ser mayor de edad");
        }
        if (clienteRq.getGenero() != null && !clienteRq.getGenero().isBlank()
                && !GENEROS.contains(clienteRq.getGenero())) {
            throw new BadRequestException("El género debe ser M, F u O");
        }
        if (clienteRq.getTelefono() != null && !clienteRq.getTelefono().isBlank()
                && !clienteRq.getTelefono().matches("\\d{7,15}")) {
            throw new BadRequestException("El teléfono debe tener entre 7 y 15 dígitos");
        }
    }

    /**
     * Arma la respuesta de exito.
     *
     * @param mensaje mensaje para el usuario.
     * @return respuesta.
     */
    private MiRespuestaRS respuestaExito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
