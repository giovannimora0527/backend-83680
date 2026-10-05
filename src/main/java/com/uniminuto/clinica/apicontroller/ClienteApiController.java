package com.uniminuto.clinica.apicontroller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.uniminuto.clinica.api.ClienteApi;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.service.ClienteService;

@RestController
public class ClienteApiController implements ClienteApi {

    private final ClienteService clienteService;

    public ClienteApiController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public ResponseEntity<List<Cliente>> listarClientes()
            throws BadRequestException {

        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @Override
    public ResponseEntity<Cliente> crearCliente(Cliente cliente)
            throws BadRequestException {

        return ResponseEntity.ok(clienteService.crearCliente(cliente));
    }

    @Override
    public ResponseEntity<Cliente> actualizarCliente(Long id, Cliente cliente)
            throws BadRequestException {

        return ResponseEntity.ok(
                clienteService.actualizarCliente(id, cliente)
        );
    }

    @Override
    public ResponseEntity<Cliente> cambiarEstado(Long id, Boolean activo)
            throws BadRequestException {

        return ResponseEntity.ok(
                clienteService.cambiarEstado(id, activo)
        );
    }
}