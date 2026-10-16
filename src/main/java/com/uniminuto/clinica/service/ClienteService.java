package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cliente;
import org.apache.coyote.BadRequestException;

import java.util.List;

/** Define las operaciones de negocio para clientes. */
public interface ClienteService {

    /** Lista los clientes activos. */
    List<Cliente> listarClientes() throws BadRequestException;

    /** Consulta un cliente por su identificador. */
    Cliente consultarCliente(Long clienteId) throws BadRequestException;
}
