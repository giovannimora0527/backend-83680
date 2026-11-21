package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/** Permite consultar y guardar clientes. */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Lista los clientes activos ordenados por nombres y apellidos. */
    List<Cliente> findByActivoTrueOrderByNombresAscApellidosAsc();
    List<Cliente> findAllByOrderByNombresAscApellidosAsc();
    boolean existsByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
    boolean existsByTipoDocumentoAndNumeroDocumentoAndClienteIdNot(
            String tipoDocumento, String numeroDocumento, Long clienteId);
}
