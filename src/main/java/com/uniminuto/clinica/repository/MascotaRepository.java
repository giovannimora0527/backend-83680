package com.uniminuto.clinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uniminuto.clinica.entity.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Integer> {

    List<Mascota> findByClienteClienteId(Long clienteId);

    List<Mascota> findByRazaRazaId(Integer razaId);
}