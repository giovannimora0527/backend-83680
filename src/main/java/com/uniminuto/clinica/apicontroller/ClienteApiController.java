package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClienteApi;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
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
    public ResponseEntity<List<Cliente>> listarTodosClientes() {
        return ResponseEntity.ok(clienteService.listarTodosClientes());
    }

    @Override
    /** Delega la consulta de un cliente. */
    public ResponseEntity<Cliente> consultarCliente(Long clienteId) throws BadRequestException {
        return ResponseEntity.ok(this.clienteService.consultarCliente(clienteId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarCliente(ClienteRq clienteRq) throws BadRequestException {
        return ResponseEntity.status(201).body(clienteService.guardarCliente(clienteRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCliente(ClienteRq clienteRq) throws BadRequestException {
        return ResponseEntity.ok(clienteService.actualizarCliente(clienteRq));
    }
}
