package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.Medicamento;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
/** Resultado de la carga masiva de medicamentos. */
public class CargaMasivaMedicamentoRS {

    /** Total de filas leídas del archivo. */
    private int totalRegistros;

    /** Cantidad de registros válidos para importar. */
    private int registrosValidos;

    /** Cantidad de registros con errores. */
    private int registrosInvalidos;

    /** Registros que fueron importados correctamente. */
    private List<Medicamento> importados = new ArrayList<>();

    /** Errores detectados durante la validación de fila. */
    private List<ErrorCargaMasivaMedicamento> errores = new ArrayList<>();
}
