package com.uniminuto.clinica.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entidad que representa una receta médica.
 */
@Data
@Entity
@Table(name = "formula_medica_legacy")
public class Receta implements Serializable {

    /**
     * Id serializable..,
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la receta.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Identificador de la cita asociada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;

    /**
     * Identificador del medicamento asociado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    /**
     * Dosis prescrita.
     */
    @Column(name = "dosis", nullable = false, columnDefinition = "text")
    private String dosis;

    /**
     * Indicaciones adicionales.
     */
    @Column(name = "indicaciones", columnDefinition = "text")
    private String indicaciones;

    @Column(name = "cantidad_prescrita", precision = 12, scale = 3)
    private BigDecimal cantidadPrescrita = BigDecimal.ONE;

    @Column(name = "estado_despacho", length = 50)
    private String estadoDespacho = "PENDIENTE_DESPACHO";

    @Column(name = "fecha_creacion_registro")
    private LocalDateTime fechaCreacionRegistro;
}
