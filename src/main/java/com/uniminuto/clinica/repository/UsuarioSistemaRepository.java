package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.UsuarioSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {
    Optional<UsuarioSistema> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);

    @Query("select usuario.cliente.clienteId from UsuarioSistema usuario where usuario.id = :id")
    Optional<Long> findClienteIdById(@Param("id") Long id);
}
