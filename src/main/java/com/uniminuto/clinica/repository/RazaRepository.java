package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de las razas.
 */
public interface RazaRepository extends JpaRepository<Raza, Integer> {

    /**
     * Indica si ya existe una raza con ese nombre y esa especie.
     *
     * @param nombre  nombre de la raza.
     * @param especie especie de la raza.
     * @return true si la raza ya existe.
     */
    boolean existsByNombreAndEspecie(String nombre, String especie);

}
