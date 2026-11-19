package com.uniminuto.clinica.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_factura")
@Data
/** Representa un ítem (servicio médico o medicamento dispensado) con precios históricos congelados en la factura. */
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    /** Identificador del detalle. */
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    /** Factura a la que pertenece este detalle. */
    private Factura factura;

    @Column(name = "tipo_item", nullable = false, length = 20)
    /** Tipo de ítem: SERVICIO o MEDICAMENTO. */
    private String tipoItem;

    @Column(name = "codigo_servicio", length = 60)
    /** Código identificador del servicio para impedir doble cobro en la misma atención. */
    private String codigoServicio;

    @Column(name = "formula_medica_id")
    /** Identificador de la fórmula médica de origen cuando es MEDICAMENTO. */
    private Long formulaMedicaId;

    @Column(name = "medicamento_id")
    /** Identificador del medicamento del inventario cuando es MEDICAMENTO. */
    private Long medicamentoId;

    @Column(name = "concepto", nullable = false, length = 250)
    /** Descripción histórica congelada del servicio o medicamento. */
    private String concepto;

    @Column(name = "cantidad", nullable = false)
    /** Cantidad cobrada (para medicamentos, corresponde a la cantidad efectivamente dispensada). */
    private Integer cantidad;

    @Column(name = "precio_unitario_historico", nullable = false, precision = 12, scale = 2)
    /** Precio unitario congelado en el momento de generar la factura. */
    private BigDecimal precioUnitarioHistorico;

    @Column(name = "descuento_item", nullable = false, precision = 12, scale = 2)
    /** Descuento específico aplicado a esta línea. */
    private BigDecimal descuentoItem = BigDecimal.ZERO;

    @Column(name = "subtotal_item", nullable = false, precision = 12, scale = 2)
    /** Subtotal de la línea: (cantidad * precioUnitarioHistorico) - descuentoItem. */
    private BigDecimal subtotalItem;
}
