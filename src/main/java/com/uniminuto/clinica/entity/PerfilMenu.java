package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Relacion entre un perfil y los menus que puede ver.
 */
@Entity
@Table(name = "perfil_menu")
@Data
public class PerfilMenu {

    /** Id de la relacion. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perfil_menu_id")
    private Integer perfilMenuId;

    /** Id del perfil. */
    @Column(name = "perfil_id", nullable = false)
    private Integer perfilId;

    /** Id del menu. */
    @Column(name = "menu_id", nullable = false)
    private Integer menuId;
}
