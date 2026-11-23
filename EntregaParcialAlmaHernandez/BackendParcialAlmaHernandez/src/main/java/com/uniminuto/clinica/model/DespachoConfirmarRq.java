package com.uniminuto.clinica.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DespachoConfirmarRq {
    @NotNull(message = "La fórmula es obligatoria.")
    private Long formulaId;

    @NotEmpty(message = "Debe seleccionar al menos un lote.")
    private List<DespachoItemRq> items;
}
