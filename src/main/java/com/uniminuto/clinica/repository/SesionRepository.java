package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de la tabla session.
 */
@Repository
public interface SesionRepository extends JpaRepository<Sesion, Integer> {

    /**
     * Busca la sesion por su token.
     *
     * @param token token enviado por el front.
     * @return la sesion si existe.
     */
    Optional<Sesion> findByToken(String token);
}
