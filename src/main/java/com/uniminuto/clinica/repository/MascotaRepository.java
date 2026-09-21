package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a los datos de las mascotas.
 */
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    /**
     * Obtiene todas las mascotas ordenadas alfabéticamente por nombre.
     *
     * @return la lista de mascotas ordenada.
     */
    List<Mascota> findAllByOrderByNombreMascotaAsc();

    /**
     * Obtiene las mascotas de un cliente ordenadas alfabéticamente por nombre.
     *
     * @param cliente propietario de las mascotas.
     * @return las mascotas del cliente.
     */
    List<Mascota> findAllByClienteOrderByNombreMascotaAsc(Cliente cliente);

    /**
     * Obtiene las mascotas de una raza ordenadas alfabéticamente por nombre.
     *
     * @param raza raza de las mascotas.
     * @return las mascotas de la raza.
     */
    List<Mascota> findAllByRazaOrderByNombreMascotaAsc(Raza raza);
}
