package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de la tabla menu.
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    /**
     * Menus activos que tiene asignados un perfil.
     *
     * @param perfilId id del perfil.
     * @return menus del perfil ordenados.
     */
    @Query("SELECT m FROM Menu m WHERE m.activo = true AND m.menuId IN "
            + "(SELECT pm.menuId FROM PerfilMenu pm WHERE pm.perfilId = :perfilId) ORDER BY m.menuId")
    List<Menu> findMenusPorPerfil(Integer perfilId);
}
