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

/**
 * Medicamento o producto del inventario. Las cantidades y precios de compra
 * estan en sus lotes (MedicamentoLote).
 */
@Entity
@Data
@Table(name = "medicamento")
public class Medicamento {

    /** Tipo: medicamento (requiere registro Invima). */
    public static final String TIPO_MEDICAMENTO = "MEDICAMENTO";
    /** Tipo: utensilio o insumo (no requiere registro Invima). */
    public static final String TIPO_UTENSILIO = "UTENSILIO";

    /** Id. */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo interno de 8 caracteres alfanumericos. */
    @Column(name = "codigo", nullable = false, length = 8)
    private String codigo;

    /** Nombre. */
    @Column(name = "nombre")
    private String nombre;

    /** Descripcion. */
    @Column(name = "descripcion")
    private String descripcion;

    /** Presentacion (tabletas, frasco...). */
    @Column(name = "presentacion")
    private String presentacion;

    /** MEDICAMENTO o UTENSILIO. */
    @Column(name = "tipo", nullable = false)
    private String tipo;

    /** Registro Invima (obligatorio para medicamentos). */
    @Column(name = "registro_invima")
    private String registroInvima;

    /** Precio de venta al publico. */
    @Column(name = "precio_venta", nullable = false)
    private BigDecimal precioVenta;

    /** IVA que se cobra al venderlo. */
    @Column(name = "iva_porcentaje", nullable = false)
    private BigDecimal ivaPorcentaje;

    /** Stock minimo permitido. */
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    /** Fecha del proximo pedido. */
    @Column(name = "fecha_proximo_pedido")
    private LocalDate fechaProximoPedido;

    /** Si esta activo. */
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /** Campo heredado del modelo anterior (ahora va en el lote). */
    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    /** Campo heredado del modelo anterior (ahora va en el lote). */
    @Column(name = "fecha_vence")
    private LocalDate fechaVence;

    /** Fecha de creacion del registro. */
    @Column(name = "fecha_creacion_registro")
    private LocalDateTime fechaCreacionRegistro;

    /** Fecha de modificacion del registro. */
    @Column(name = "fecha_modificacion_registro")
    private LocalDateTime fechaModificacionRegistro;
}
