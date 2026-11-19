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
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_factura")
@Data
/** Representa un abono o pago asociado a una factura. */
public class PagoFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    /** Identificador del pago. */
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    /** Factura a la que se abona este pago. */
    private Factura factura;

    @Column(name = "fecha_pago", nullable = false)
    /** Fecha y hora en que se registró el pago. */
    private LocalDateTime fechaPago;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    /** Valor monetario abonado. */
    private BigDecimal monto;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    /** Medio de pago: EFECTIVO, TARJETA, TRANSFERENCIA u OTRO. */
    private String metodoPago;

    @Column(name = "referencia_pago", length = 100)
    /** Número de comprobante, recibo o referencia bancaria. */
    private String referenciaPago;

    @Column(name = "usuario_registro", length = 100)
    /** Usuario o perfil responsable que registró el pago. */
    private String usuarioRegistro;

    @Column(name = "observacion", length = 300)
    /** Nota u observación del pago. */
    private String observacion;
}
