package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.models.ClienteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @GetMapping(value = "/listar-todos", produces = "application/json")
    ResponseEntity<List<Cliente>> listarTodosClientes();

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

    @PostMapping(value = "/guardar", consumes = "application/json", produces = "application/json")
    ResponseEntity<MiRespuestaRS> guardarCliente(@RequestBody ClienteRq clienteRq) throws BadRequestException;

    @PostMapping(value = "/actualizar", consumes = "application/json", produces = "application/json")
    ResponseEntity<MiRespuestaRS> actualizarCliente(@RequestBody ClienteRq clienteRq) throws BadRequestException;
}
