package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cliente;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cliente")
public interface ClienteApi {

    /**
     * Lista los clientes activos.
     *
     * @return lista de clientes ordenada por nombre.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/listar", produces = "application/json")
    ResponseEntity<List<Cliente>> listarClientes()
            throws BadRequestException;

    /**
     * Consulta un cliente por su identificador.
     *
     * @param clienteId identificador del cliente.
     * @return cliente encontrado.
     * @throws BadRequestException excepcion.
     */
    @GetMapping(value = "/consultar", produces = "application/json")
    ResponseEntity<Cliente> consultarCliente(@RequestParam Long clienteId)
            throws BadRequestException;
}
