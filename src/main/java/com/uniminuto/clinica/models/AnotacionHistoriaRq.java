package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class AnotacionHistoriaRq {
    private Long anotacionHistoriaId;
    private Long historiaMedicaId;
    private Long medicoId;
    private String descripcion;
}
