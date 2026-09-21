package com.uniminuto.clinica.service;

import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio con la logica de las anotaciones de historia medica.
 */
public interface AnotacionHistoriaService {

    /**
     * Lista las anotaciones entre dos fechas (incluidas), de la mas reciente a la mas antigua.
     *
     * @param fechaInicial primer dia del rango.
     * @param fechaFinal   ultimo dia del rango.
     * @return las anotaciones del rango.
     */
    List<AnotacionHistoriaRs> listarAnotaciones(LocalDate fechaInicial, LocalDate fechaFinal);

    /**
     * Guarda una anotacion nueva en una historia medica.
     *
     * @param anotacionRq datos de la anotacion.
     * @return respuesta de exito.
     */
    MiRespuestaRS guardarAnotacion(AnotacionHistoriaRq anotacionRq);

    /**
     * Actualiza el medico y la descripcion de una anotacion que ya existe.
     *
     * @param anotacionRq datos de la anotacion, incluido su id.
     * @return respuesta de exito.
     */
    MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq anotacionRq);
}
