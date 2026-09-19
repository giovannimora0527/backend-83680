package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/** Permite consultar y guardar mascotas. */
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    /** Lista todas las mascotas ordenadas por nombre. */
    List<Mascota> findAllByOrderByNombreMascotaAsc();

    /** Lista las mascotas de un cliente ordenadas por nombre. */
    List<Mascota> findAllByClienteOrderByNombreMascotaAsc(Cliente cliente);

    /** Lista las mascotas de una raza ordenadas por nombre. */
    List<Mascota> findAllByRazaOrderByNombreMascotaAsc(Raza raza);
}
