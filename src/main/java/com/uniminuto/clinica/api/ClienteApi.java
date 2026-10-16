package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Rutas de los servicios de los clientes.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/cliente")
public interface ClienteApi {

    /**
     * Lista todos los clientes ordenados por nombre.
     *
     * @return lista de clientes.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Cliente>> listarClientes();

    /**
     * Consulta un cliente por su id.
     *
     * @param clienteId id del cliente.
     * @return el cliente encontrado.
     */
    @GetMapping(value = "/consultar", produces = {"application/json"})
    ResponseEntity<Cliente> consultarCliente(@RequestParam Long clienteId);

    /**
     * Crea un cliente nuevo.
     *
     * @param clienteRq datos del cliente.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarCliente(@RequestBody ClienteRq clienteRq);

    /**
     * Actualiza un cliente existente.
     *
     * @param clienteRq datos del cliente con su id.
     * @return respuesta de exito.
     */
    @PutMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarCliente(@RequestBody ClienteRq clienteRq);
}
