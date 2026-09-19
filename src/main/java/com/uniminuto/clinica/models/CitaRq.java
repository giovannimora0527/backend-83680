package com.uniminuto.clinica.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CitaRq {
    private Integer clienteId;
    private Integer mascotaId;
    private Integer medicoId;
    private LocalDateTime fechaHora;
    private String estado;
    private String motivo;
}