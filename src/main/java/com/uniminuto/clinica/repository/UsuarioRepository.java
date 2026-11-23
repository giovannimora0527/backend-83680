package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de la tabla usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario.
     * @return el usuario si existe.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Revisa si el nombre de usuario ya existe.
     *
     * @param username nombre de usuario.
     * @return true si existe.
     */
    boolean existsByUsername(String username);

    /**
     * Lista los usuarios ordenados por nombre de usuario.
     *
     * @return usuarios.
     */
    List<Usuario> findAllByOrderByUsernameAsc();
}
