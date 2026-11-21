package com.uniminuto.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "carga_masiva_medicamento")
@Data
public class CargaMasivaMedicamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "total_registros", nullable = false)
    private Integer totalRegistros;

    @Column(name = "registros_importados", nullable = false)
    private Integer registrosImportados;

    @Column(name = "registros_invalidos", nullable = false)
    private Integer registrosInvalidos;

    @Column(name = "usuario_registro", nullable = false, length = 100)
    private String usuarioRegistro;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
