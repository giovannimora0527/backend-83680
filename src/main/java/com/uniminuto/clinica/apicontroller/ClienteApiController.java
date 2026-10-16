package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.ClienteApi;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de los clientes, solo delega al servicio.
 */
@RestController
public class ClienteApiController implements ClienteApi {

    /** Servicio con la logica de los clientes. */
    @Autowired
    private ClienteService clienteService;

    @Override
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @Override
    public ResponseEntity<Cliente> consultarCliente(Long clienteId) {
        return ResponseEntity.ok(clienteService.consultarCliente(clienteId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarCliente(ClienteRq clienteRq) {
        return ResponseEntity.ok(clienteService.guardarCliente(clienteRq));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarCliente(ClienteRq clienteRq) {
        return ResponseEntity.ok(clienteService.actualizarCliente(clienteRq));
    }
}
