package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClienteApi;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.service.ClienteService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/** Controlador que expone los servicios de clientes. */
public class ClienteApiController implements ClienteApi {

    @Autowired
    /** Servicio con la lógica de clientes. */
    private ClienteService clienteService;

    @Override
    /** Delega la consulta de clientes activos. */
    public ResponseEntity<List<Cliente>> listarClientes() throws BadRequestException {
        return ResponseEntity.ok(this.clienteService.listarClientes());
    }

    @Override
    /** Delega la consulta de un cliente. */
    public ResponseEntity<Cliente> consultarCliente(Long clienteId) throws BadRequestException {
        return ResponseEntity.ok(this.clienteService.consultarCliente(clienteId));
    }
}
