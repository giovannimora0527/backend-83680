package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.util.List;

/** Define las operaciones de negocio para fórmulas médicas y dispensación. */
public interface FormulaMedicaService {
    /** Crea una fórmula médica evitando duplicados en la misma cita y validando stock sin descontarlo. */
    MiRespuestaRS crearFormulaMedica(FormulaMedicaRq formulaRq);

    /** Lista las fórmulas médicas registradas. */
    List<FormulaMedica> listarFormulasMedicas();

    /** Lista las fórmulas médicas asociadas a una cita (atención médica). */
    List<FormulaMedica> listarFormulasPorCita(Long citaId);

    /** Lista las fórmulas médicas asociadas a una mascota. */
    List<FormulaMedica> listarFormulasPorMascota(Integer mascotaId);

    /** Consulta una fórmula médica por su identificador. */
    FormulaMedica consultarFormulaMedica(Long formulaId);

    /** Actualiza los datos permitidos de una fórmula médica. */
    MiRespuestaRS actualizarFormulaMedica(FormulaMedicaRq formulaRq);

    /** Inactiva una fórmula médica en lugar de eliminarla. */
    MiRespuestaRS inactivarFormulaMedica(Long formulaId);

    /** Registra la entrega real del medicamento y descuenta el inventario. */
    MiRespuestaRS dispensarFormula(Long formulaId, Integer cantidad, String observacion);

    /** Registra la entrega real del medicamento, descuenta el inventario y guarda el usuario responsable. */
    MiRespuestaRS dispensarFormula(Long formulaId, Integer cantidad, String observacion, String usuario);

    /** Consulta el historial de movimientos del medicamento asociado a la fórmula. */
    List<MovimientoMedicamento> consultarHistorialPorFormula(Long formulaId);
}
