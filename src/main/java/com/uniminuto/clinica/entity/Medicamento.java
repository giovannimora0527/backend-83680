package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "medicamento")
/** Representa un medicamento o producto médico del inventario de la clínica. */
public class Medicamento {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** Identificador del producto. */
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    /** Nombre del medicamento o producto. */
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    /** Descripción o información adicional. */
    private String descripcion;

    @Column(name = "presentacion", length = 100)
    /** Presentación comercial del producto. */
    private String presentacion;

    @Column(name = "codigo", nullable = false, length = 8, unique = true)
    /** Código interno alfanumérico de 8 caracteres. */
    private String codigo;

    @Column(name = "lote", length = 50)
    /** Número o identificador del lote. */
    private String lote;

    @Column(name = "registro_invima", length = 50)
    /** Registro sanitario Invima cuando aplica. */
    private String registroInvima;

    @Column(name = "requiere_registro_invima", nullable = false)
    /** Indica si el producto exige registro Invima. */
    private Boolean requiereRegistroInvima = false;

    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    /** Precio de compra del producto. */
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    /** Precio de venta al público. */
    private BigDecimal precioVenta;

    @Column(name = "cantidad_disponible", nullable = false)
    /** Cantidad actual disponible en inventario. */
    private Integer cantidadDisponible;

    @Column(name = "stock_minimo", nullable = false)
    /** Nivel mínimo de inventario permitido. */
    private Integer stockMinimo;

    @Column(name = "fecha_compra", nullable = false)
    /** Fecha de compra del producto. */
    private LocalDate fechaCompra;

    @Column(name = "fecha_ingreso_inventario", nullable = false)
    /** Fecha de ingreso del producto al inventario. */
    private LocalDate fechaIngresoInventario;

    @Column(name = "fecha_vence", nullable = false)
    /** Fecha de vencimiento del producto. */
    private LocalDate fechaVence;

    @Column(name = "fecha_proximo_pedido")
    /** Fecha estimada del próximo pedido. */
    private LocalDate fechaProximoPedido;

    @Column(name = "activo", nullable = false)
    /** Indica si el producto se encuentra activo. */
    private Boolean activo = true;

    @Column(name = "fecha_creacion_registro", nullable = false)
    /** Fecha de creación del registro. */
    private LocalDateTime fechaCreacionRegistro;

    @Column(name = "fecha_modificacion_registro")
    /** Fecha de la última modificación. */
    private LocalDateTime fechaModificacionRegistro;
}
