package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/**
 * Servicio con la logica de negocio de los clientes.
 */
public interface ClienteService {

    /**
     * Lista todos los clientes ordenados por nombre.
     *
     * @return lista de clientes.
     */
    List<Cliente> listarClientes();

    /**
     * Busca un cliente por su id.
     *
     * @param clienteId id del cliente.
     * @return el cliente, o error si no existe.
     */
    Cliente consultarCliente(Long clienteId);

    /**
     * Crea un cliente validando que el documento no este repetido.
     *
     * @param clienteRq datos del cliente.
     * @return respuesta de exito.
     */
    MiRespuestaRS guardarCliente(ClienteRq clienteRq);

    /**
     * Actualiza un cliente existente.
     *
     * @param clienteRq datos del cliente con su id.
     * @return respuesta de exito.
     */
    MiRespuestaRS actualizarCliente(ClienteRq clienteRq);
}
