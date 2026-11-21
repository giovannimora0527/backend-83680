package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;

import java.util.List;

/** Define las operaciones de negocio para clientes. */
public interface ClienteService {

    /** Lista los clientes activos. */
    List<Cliente> listarClientes() throws BadRequestException;

    List<Cliente> listarTodosClientes();

    /** Consulta un cliente por su identificador. */
    Cliente consultarCliente(Long clienteId) throws BadRequestException;

    MiRespuestaRS guardarCliente(ClienteRq clienteRq) throws BadRequestException;

    MiRespuestaRS actualizarCliente(ClienteRq clienteRq) throws BadRequestException;
}
