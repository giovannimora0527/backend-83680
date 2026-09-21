package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a los datos de los clientes.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
