package com.uniminuto.clinica.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase Entidad Medicamento.
 */
@Data
@Entity
@Table(name = "medicamento")
public class Medicamento implements Serializable {

    /**
     * Id serializable.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único del medicamento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "codigo", length = 8, unique = true)
    private String codigo;

    /**
     * Nombre del medicamento.
     */
    @Column(name = "nombre", length = 100, nullable = false, unique = true)
    private String nombre;

    /**
     * Descripción del medicamento.
     */
    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    /**
     * Presentación del medicamento.
     */
    @Column(name = "presentacion", length = 100)
    private String presentacion;

    @Column(name = "tipo_producto", length = 20, nullable = false)
    private String tipoProducto = "MEDICAMENTO";

    @Column(name = "precio_compra", precision = 12, scale = 2)
    private java.math.BigDecimal precioCompra;

    @Column(name = "precio_venta", precision = 12, scale = 2)
    private java.math.BigDecimal precioVenta;

    @Column(name = "cantidad_actual", precision = 12, scale = 3, nullable = false)
    private java.math.BigDecimal cantidadActual = java.math.BigDecimal.ZERO;

    @Column(name = "stock_minimo", precision = 12, scale = 3, nullable = false)
    private java.math.BigDecimal stockMinimo = java.math.BigDecimal.ZERO;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_proximo_pedido")
    private LocalDate fechaProximoPedido;

    @Column(name = "registro_invima", length = 100)
    private String registroInvima;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Fecha de compra del medicamento.
     */
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    /*** Fecha de vencimiento del medicamento.
     */
    @Column(name = "fecha_vence", nullable = false)
    private LocalDate fechaVence;

    /**
     * Fecha de creación del registro.
     */
    @javax.persistence.Column(name = "fecha_creacion_registro", nullable = false)
    private LocalDateTime fechaCreacionRegistro;

    /**
     * Fecha de modificación del registro.
     */
    @javax.persistence.Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;
}
