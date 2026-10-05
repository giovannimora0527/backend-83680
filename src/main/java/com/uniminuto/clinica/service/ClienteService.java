package com.uniminuto.clinica.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.uniminuto.clinica.entity.Cliente;

public interface ClienteService {

    List<Cliente> listarClientes() throws BadRequestException;

    Cliente crearCliente(Cliente cliente) throws BadRequestException;

    Cliente actualizarCliente(Long clienteId, Cliente cliente) throws BadRequestException;

    Cliente cambiarEstado(Long clienteId, Boolean activo) throws BadRequestException;
}