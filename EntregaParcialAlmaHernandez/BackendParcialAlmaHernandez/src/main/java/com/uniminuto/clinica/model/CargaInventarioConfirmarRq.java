package com.uniminuto.clinica.model;

import lombok.Data;

import java.util.List;

@Data
public class CargaInventarioConfirmarRq {
    private String nombreArchivo;
    private List<MedicamentoRq> registros;
}
