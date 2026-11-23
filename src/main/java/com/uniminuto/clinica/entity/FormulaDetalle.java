package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Detalle de una formula medica: un medicamento prescrito con su dosis y cantidad.
 */
@Entity
@Data
@Table(name = "formula_detalle")
public class FormulaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Formula (cabecera). */
    @Column(name = "formula_id", nullable = false)
    private Long formulaId;

    /** Medicamento prescrito. */
    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    /** Dosis (ej: 1 tableta). */
    @Column(name = "dosis", nullable = false)
    private String dosis;

    /** Frecuencia (ej: cada 8 horas). */
    @Column(name = "frecuencia", nullable = false)
    private String frecuencia;

    /** Duracion del tratamiento en dias. */
    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    /** Cantidad total prescrita (unidades a entregar). */
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    /** Via de administracion (oral, intramuscular...). */
    @Column(name = "via_administracion", nullable = false)
    private String viaAdministracion;

    /** Indicaciones especiales. */
    @Column(name = "indicaciones", columnDefinition = "TEXT")
    private String indicaciones;

    /** Unidades ya entregadas por despacho. */
    @Column(name = "cantidad_entregada", nullable = false)
    private Integer cantidadEntregada = 0;
}
