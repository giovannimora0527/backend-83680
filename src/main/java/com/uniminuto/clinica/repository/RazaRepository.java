package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Permite consultar y guardar razas. */
public interface RazaRepository extends JpaRepository<Raza, Integer> {

    /** Busca una raza por su nombre. */
    Optional<Raza> findByNombre(String nombre);

    /** Busca una raza por su especie. */
    Optional<Raza> findByEspecie(String especie);

}
