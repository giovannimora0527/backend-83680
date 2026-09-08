package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RazaRepository extends JpaRepository<Raza, Integer> {

    Optional<Raza> findByNombre(String nombre);

    Optional<Raza> findByEspecie(String especie);

}
