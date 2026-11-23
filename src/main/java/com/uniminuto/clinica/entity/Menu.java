package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Opcion del menu lateral. Cada perfil ve solo los menus asignados en perfil_menu.
 */
@Entity
@Table(name = "menu")
@Data
public class Menu {

    /** Id del menu. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Integer menuId;

    /** Codigo corto. */
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    /** Texto que se muestra. */
    @Column(name = "title", nullable = false)
    private String title;

    /** Ruta del front. */
    @Column(name = "url", nullable = false, columnDefinition = "LONGTEXT")
    private String url;

    /** Clase del icono. */
    @Column(name = "icon", nullable = false)
    private String icon;

    /** Si esta activo. */
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    /** Fecha de creacion. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha de modificacion. */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
