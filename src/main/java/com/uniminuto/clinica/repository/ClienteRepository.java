package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Lista los clientes ordenados por nombres y apellidos.
     *
     * @return lista de clientes.
     */
    List<Cliente> findAllByOrderByNombresAscApellidosAsc();

    /**
     * Revisa si ya existe un cliente con ese documento.
     *
     * @param numeroDocumento numero de documento.
     * @return true si existe.
     */
    boolean existsByNumeroDocumento(String numeroDocumento);

    /**
     * Revisa si otro cliente (distinto al que se edita) tiene ese documento.
     *
     * @param numeroDocumento numero de documento.
     * @param clienteId       id del cliente que se edita.
     * @return true si existe.
     */
    boolean existsByNumeroDocumentoAndClienteIdNot(String numeroDocumento, Long clienteId);
}
