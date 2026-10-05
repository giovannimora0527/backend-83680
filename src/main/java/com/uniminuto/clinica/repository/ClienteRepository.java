package com.uniminuto.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniminuto.clinica.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Validar si ya existe un cliente con ese documento
    boolean existsByTipoDocumentoAndNumeroDocumento(
            String tipoDocumento,
            String numeroDocumento
    );

    // Validar duplicados al editar,
    // excluyendo al cliente que estamos modificando
    boolean existsByTipoDocumentoAndNumeroDocumentoAndClienteIdNot(
            String tipoDocumento,
            String numeroDocumento,
            Long clienteId
    );
}