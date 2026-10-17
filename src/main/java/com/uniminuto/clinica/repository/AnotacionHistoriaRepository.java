package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para la entidad {@link AnotacionHistoria}.
 */
@Repository
public interface AnotacionHistoriaRepository extends JpaRepository<AnotacionHistoria, Long> {

    /**
     * Busca las anotaciones de una historia clínica con filtro de texto, paginadas.
     *
     * @param historiaId id de la historia clínica.
     * @param texto      texto libre (descripción, observación o diagnóstico); vacío si no aplica.
     * @param pageable   paginación y orden.
     * @return página de anotaciones.
     */
    @Query("""
        SELECT a FROM AnotacionHistoria a
        WHERE a.historia.id = :historiaId
          AND (:texto = ''
               OR LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(a.observacion) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(a.diagnostico) LIKE LOWER(CONCAT('%', :texto, '%')))
    """)
    Page<AnotacionHistoria> buscarPorHistoria(@Param("historiaId") Long historiaId,
                                              @Param("texto") String texto,
                                              Pageable pageable);
}