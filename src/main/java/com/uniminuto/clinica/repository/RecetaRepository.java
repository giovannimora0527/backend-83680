package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Receta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {
    List<Receta> findByCitaAndMedicamento(Cita cita, Medicamento medicamento);

        /**
     * Obtiene las fórmulas médicas ordenadas de la más reciente
     * a la más antigua según su fecha de creación.
     *
     * @return lista de fórmulas médicas ordenadas por fecha de creación.
     */
    List<Receta> findAllByOrderByFechaCreacionRegistroDesc();
    

}
