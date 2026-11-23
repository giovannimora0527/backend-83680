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
 * Log de cada carga masiva de inventario.
 */
@Entity
@Data
@Table(name = "carga_inventario_log")
public class CargaInventarioLog {

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Usuario que hizo la carga. */
    @Column(name = "usuario_id")
    private Long usuarioId;

    /** Fecha de la carga. */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    /** Nombre del archivo. */
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    /** Filas leidas. */
    @Column(name = "total_filas", nullable = false)
    private Integer totalFilas;

    /** Filas importadas. */
    @Column(name = "filas_ok", nullable = false)
    private Integer filasOk;

    /** Filas rechazadas. */
    @Column(name = "filas_error", nullable = false)
    private Integer filasError;

    /** Detalle de los errores (fila y motivo). */
    @Column(name = "detalle", columnDefinition = "LONGTEXT")
    private String detalle;
}
