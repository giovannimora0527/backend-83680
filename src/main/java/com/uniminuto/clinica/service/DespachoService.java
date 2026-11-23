package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.ConfirmarDespachoRq;
import com.uniminuto.clinica.models.DespachoFormulaRs;
import com.uniminuto.clinica.models.DespachoRs;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDate;
import java.util.List;

/**
 * Despacho y dispensacion de medicamentos (perfil DESPACHO_MEDICO).
 */
public interface DespachoService {

    /**
     * Formulas pendientes de despacho con filtros.
     *
     * @param buscar texto: numero de formula, propietario o mascota.
     * @param fecha  fecha de generacion (opcional).
     * @return formulas pendientes.
     */
    List<FormulaMedicaRs> listarPendientes(String buscar, LocalDate fecha);

    /** @return detalle de una formula para despacharla. */
    DespachoFormulaRs consultarFormula(Long formulaId);

    /** @return respuesta al pasar la formula a EN_PREPARACION. */
    MiRespuestaRS iniciarPreparacion(Long formulaId);

    /** @return respuesta al confirmar la entrega (transaccional). */
    MiRespuestaRS confirmarDespacho(ConfirmarDespachoRq rq);

    /** @return respuesta al marcar la formula como pendiente por disponibilidad. */
    MiRespuestaRS marcarSinDisponibilidad(Long formulaId, String observacion);

    /** @return respuesta al cancelar el despacho de una formula. */
    MiRespuestaRS cancelarDespacho(Long formulaId, String motivo);

    /** @return respuesta al revertir un despacho confirmado (solo Administrador). */
    MiRespuestaRS revertirDespacho(Long despachoId, String motivo);

    /** @return despachos realizados entre dos fechas (log de despacho). */
    List<DespachoRs> historial(LocalDate desde, LocalDate hasta);

    /** @return comprobante de despacho en PDF. */
    byte[] comprobantePdf(Long despachoId);
}
