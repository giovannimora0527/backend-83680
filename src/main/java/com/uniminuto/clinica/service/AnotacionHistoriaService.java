package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio de negocio para las anotaciones de las historias clínicas.
 */
public interface AnotacionHistoriaService {

    /**
     * Lista las anotaciones de una historia clínica, paginadas, con filtro de texto.
     *
     * @param historiaId id de la historia clínica.
     * @param texto      texto libre o null.
     * @param pageable   paginación y orden.
     * @return página de anotaciones.
     */
    Page<AnotacionHistoria> listarAnotaciones(Long historiaId, String texto, Pageable pageable);

    /**
     * Consulta el detalle de una anotación.
     *
     * @param id id de la anotación.
     * @return la anotación encontrada.
     */
    AnotacionHistoria consultarAnotacion(Long id);

    /**
     * Agrega una anotación a una historia clínica existente.
     *
     * @param anotacionHistoriaRq datos de la anotación.
     * @return mensaje de confirmación.
     * @throws BadRequestException si faltan datos obligatorios.
     */
    MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq anotacionHistoriaRq) throws BadRequestException;
}